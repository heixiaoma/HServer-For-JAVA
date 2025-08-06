package cn.hserver.mcp;

import cn.hserver.core.interfaces.PluginAdapter;
import cn.hserver.core.ioc.IocUtil;
import cn.hserver.core.ioc.ref.PackageScanner;
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
public class McpPlugin implements PluginAdapter {

    private final List<McpSyncServer> mcpSyncServers=new ArrayList<>();

    @Override
    public void startApp() {
    }

    @Override
    public void startIocInit() {
    }

    @Override
    public Set<Class<?>> iocInitBeanList() {
        return new HashSet<>();
    }

    @Override
    public void iocInit(PackageScanner packageScanner) {
        //提取注解开始工作
        try {
            Set<Class<?>> annotationList = packageScanner.getAnnotationList(McpServerEndpoint.class);
            for (Class<?> aClass : annotationList) {
                McpServerEndpoint annotation = aClass.getAnnotation(McpServerEndpoint.class);
                McpSchema.ServerCapabilities serverCapabilities = McpSchema.ServerCapabilities.builder()
                        .tools(true)
                        .resources(true, true)
                        .prompts(true)
                        .logging()
                        .build();
                HServerSseServerTransportProvider hServerSseServerTransportProvider = new HServerSseServerTransportProvider(annotation.sseEndpoint());
                IocUtil.addBean(hServerSseServerTransportProvider);
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
                mcpSyncServers.add(mcpSyncServer);
                Object controllerRef = aClass.newInstance();
                IocUtil.addBean(controllerRef);
                log.info("MCP Start {}",aClass.getName());
            }
        }catch (Exception e){
            log.error(e.getMessage(),e);
        }
    }

    @Override
    public void iocInitEnd() {

    }

    @Override
    public void startInjection() {

    }

    @Override
    public void injectionEnd() {

//        this.serverProperties = serverProperties;
//        this.sseEndpoint = serverProperties.getSseEndpoint();
//        this.messageEndpoint = PathUtil.mergePath(this.sseEndpoint, "message");
//
//        if (McpChannel.STDIO.equalsIgnoreCase(serverProperties.getChannel())) {
//            //stdio 通道
//            this.mcpTransportProvider = new StdioServerTransportProvider();
//        } else {
//            //sse 通道
//            this.mcpTransportProvider = WebRxSseServerTransportProvider.builder()
//                    .messageEndpoint(this.messageEndpoint)
//                    .sseEndpoint(this.sseEndpoint)
//                    .objectMapper(new ObjectMapper())
//                    .build();
//        }

//        McpSchema.ServerCapabilities serverCapabilities = McpSchema.ServerCapabilities.builder()
//                .tools(true)
//                .resources(true, true)
//                .prompts(true)
//                .logging()
//                .build();
//        System.out.println(hServerSseServerTransportProvider);
//        McpSyncServer build = McpServer.sync(hServerSseServerTransportProvider)
//                .capabilities(serverCapabilities)
//                .serverInfo("mcp-server", "1.0").build();
//        McpServerFeatures.SyncToolSpecification syncToolSpecification = new McpServerFeatures.SyncToolSpecification();
//
//        Map<String,String> data2=new HashMap<>();
//        data2.put("type", "string");
//        data2.put("description", "名字");
//        String s ="";
//        try {
//             s = WebConstConfig.JSON.writeValueAsString(data2);
//        }catch (Exception e){
//            log.error(e.getMessage(),e);
//        }
//        System.out.println(s);
//
//        McpSchema.JsonSchema jsonSchema = new McpSchema.JsonSchema();
//        jsonSchema.setType("object");
// //* return ""inputSchema\":{\"type\":\"object\",\"properties\":{\"name\":{\"type\":\"string\",\"description\":\"名字\"}},\"required\":[\"name\"]}}]}}";
//
//        Map<String,Object> data22=new HashMap<>();
//        data22.put("name",data2);
//        jsonSchema.setProperties(data22);
//        List<String> datar=new ArrayList<>();
//                datar.add("name");
//        jsonSchema.setRequired(datar);
//
//        McpSchema.Tool tool = new McpSchema.Tool();
//        tool.setName("aaa");
//        tool.setDescription("aaa");
//        tool.setInputSchema(jsonSchema);
//        syncToolSpecification.setTool(tool);
//        syncToolSpecification.setCall((e,a)->{
//            McpSchema.CallToolResult res = new McpSchema.CallToolResult();
//            List<McpSchema.Content> data = new ArrayList<>();
//            data.add(new McpSchema.TextContent("2222"));
//            res.setContent(data);
//            return res;
//        });
//        build.addTool(syncToolSpecification);
    }
}
