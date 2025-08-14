package cn.hserver.mcp;

import cn.hserver.core.context.handler.AnnotationHandler;
import cn.hserver.core.plugin.bean.PluginInfo;
import cn.hserver.core.plugin.handler.PluginAdapter;
import cn.hserver.mcp.annotation.McpServerEndpoint;
import cn.hserver.mcp.annotation.PromptMapping;
import cn.hserver.mcp.annotation.ResourcesMapping;
import cn.hserver.mcp.annotation.ToolMapping;
import cn.hserver.mcp.function.FunctionPrompt;
import cn.hserver.mcp.function.FunctionResources;
import cn.hserver.mcp.function.FunctionTool;
import cn.hserver.modelcontextprotocol.server.McpServer;
import cn.hserver.modelcontextprotocol.server.McpServerFeatures;
import cn.hserver.modelcontextprotocol.server.McpSyncServer;
import cn.hserver.modelcontextprotocol.server.transport.HServerSseServerTransportProvider;
import cn.hserver.modelcontextprotocol.spec.McpSchema;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;
import java.util.*;

@Slf4j
public class McpPlugin extends PluginAdapter {

    public final static List<HServerSseServerTransportProvider> hServerSseServerTransportProviderList = new ArrayList<>();

    @Override
    public PluginInfo getPluginInfo() {
        return new PluginInfo.Builder().name("MCP").description("快速实现一个 MCP Server API").build();
    }

    @Override
    public void startApp() {
        AnnotationHandler.addHandler(new McpAnnotationHandler());
    }

    @Override
    public void iocStartScan(Class<?> aClass) {
        if (aClass.isAnnotationPresent(McpServerEndpoint.class)) {
            McpServerEndpoint annotation = aClass.getAnnotation(McpServerEndpoint.class);
            McpSchema.ServerCapabilities serverCapabilities = McpSchema.ServerCapabilities.builder()
                    .tools(true)
                    .resources(true, true)
                    .prompts(true)
                    .logging()
                    .build();
            HServerSseServerTransportProvider hServerSseServerTransportProvider = new HServerSseServerTransportProvider(annotation.sseEndpoint());
            hServerSseServerTransportProviderList.add(hServerSseServerTransportProvider);
            McpSyncServer mcpSyncServer = McpServer.sync(hServerSseServerTransportProvider)
                    .capabilities(serverCapabilities)
                    .serverInfo(annotation.name(), annotation.version()).build();
            //遍历方法
            for (Method declaredMethod : aClass.getDeclaredMethods()) {
                ToolMapping toolMapping = declaredMethod.getAnnotation(ToolMapping.class);
                if (toolMapping != null) {
                    McpServerFeatures.SyncToolSpecification syncToolSpecification = new McpServerFeatures.SyncToolSpecification();
                    McpSchema.Tool tool = new McpSchema.Tool();
                    tool.setName(toolMapping.name());
                    tool.setName(toolMapping.description());
                    FunctionTool functionData = new FunctionTool(aClass,declaredMethod);
                    tool.setInputSchema(functionData.getInputSchema());
                    syncToolSpecification.setTool(tool);
                    syncToolSpecification.setCall((e,a)-> functionData.invoke(a));
                    mcpSyncServer.addTool(syncToolSpecification);
                }
                ResourcesMapping resourcesMapping = declaredMethod.getAnnotation(ResourcesMapping.class);
                if (resourcesMapping != null) {
                    FunctionResources functionResources = new FunctionResources(aClass, declaredMethod);
                    if (resourcesMapping.uri().contains("}")) {
                        McpServerFeatures.SyncResourceTemplateSpecification syncResourceSpecification = new McpServerFeatures.SyncResourceTemplateSpecification();
                        McpSchema.ResourceTemplate resource = new McpSchema.ResourceTemplate();
                        resource.setUriTemplate(resourcesMapping.uri());
                        resource.setName(resourcesMapping.name());
                        resource.setDescription(resourcesMapping.description());
                        syncResourceSpecification.setResource(resource);
                        syncResourceSpecification.setReadHandler((a, b) -> functionResources.invoke(b,resourcesMapping));
                        mcpSyncServer.addResourceTemplate(syncResourceSpecification);
                    }else {
                        McpServerFeatures.SyncResourceSpecification syncResourceSpecification = new McpServerFeatures.SyncResourceSpecification();
                        McpSchema.Resource resource = new McpSchema.Resource();
                        resource.setUri(resourcesMapping.uri());
                        resource.setName(resourcesMapping.name());
                        resource.setDescription(resourcesMapping.description());
                        syncResourceSpecification.setResource(resource);
                        syncResourceSpecification.setReadHandler((a, b) -> functionResources.invoke(b,resourcesMapping));
                        mcpSyncServer.addResource(syncResourceSpecification);
                    }
                }
                PromptMapping promptMapping = declaredMethod.getAnnotation(PromptMapping.class);
                if (promptMapping != null) {
                    FunctionPrompt functionPrompt = new FunctionPrompt(aClass, declaredMethod);
                    McpServerFeatures.SyncPromptSpecification syncPromptSpecification = new McpServerFeatures.SyncPromptSpecification();
                    McpSchema.Prompt prompt = new McpSchema.Prompt();
                    prompt.setName(promptMapping.name());
                    prompt.setDescription(promptMapping.description());
                    prompt.setArguments(functionPrompt.getPromptArguments());
                    syncPromptSpecification.setPrompt(prompt);
                    syncPromptSpecification.setPromptHandler((a, b) -> functionPrompt.invoke(b,prompt));
                    mcpSyncServer.addPrompt(syncPromptSpecification);
                }
            }
            log.info("MCP Start {}",aClass.getName());
        }
    }

}
