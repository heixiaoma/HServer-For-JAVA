package cn.hserver.mcp;

import cn.hserver.core.interfaces.PluginAdapter;
import cn.hserver.core.ioc.IocUtil;
import cn.hserver.core.ioc.ref.PackageScanner;
import cn.hserver.mcp.annotation.McpServerEndpoint;
import cn.hserver.mcp.annotation.ToolMapping;
import cn.hserver.modelcontextprotocol.server.McpServer;
import cn.hserver.modelcontextprotocol.server.McpServerFeatures;
import cn.hserver.modelcontextprotocol.server.McpSyncServer;
import cn.hserver.modelcontextprotocol.server.transport.HServerSseServerTransportProvider;
import cn.hserver.modelcontextprotocol.spec.McpSchema;
import cn.hserver.modelcontextprotocol.spec.McpServerTransportProvider;
import cn.hserver.plugin.web.context.WebConstConfig;
import cn.hserver.plugin.web.interfaces.GlobalException;
import cn.hserver.plugin.web.util.ParameterUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;
import java.util.*;

@Slf4j
public class McpPlugin implements PluginAdapter {

    private HServerSseServerTransportProvider hServerSseServerTransportProvider;

    private List<McpSyncServer> mcpSyncServers=new ArrayList<>();

    @Override
    public void startApp() {
    }

    @Override
    public void startIocInit() {
    }

    @Override
    public Set<Class<?>> iocInitBeanList() {
        Set<Class<?>> classes = new HashSet<>();
        //检测这注解
        classes.add(McpServerEndpoint.class);
        return classes;
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
                        //todo 构建参数
                        tool.setInputSchema();
                        syncToolSpecification.setTool(tool);
                        syncToolSpecification.setCall((e,a)->{
                            McpSchema.CallToolResult res = new McpSchema.CallToolResult();
                            try {
                                Object bean = IocUtil.getBean(aClass);

                                //todo 参数校验

                                Object invoke = declaredMethod.invoke(bean);
                                if (invoke  instanceof McpSchema.Content) {
                                    List<McpSchema.Content> data = new ArrayList<>();
                                    data.add((McpSchema.Content)invoke);
                                    res.setContent(data);
                                } else if (invoke instanceof List){
                                    res.setContent((List<McpSchema.Content>)invoke);
                                }else {
                                    res.setIsError(true);
                                    res.setContent(new ArrayList<>());
                                    log.error("响应数据类型错误");
                                }
                            }catch (Exception e1) {
                                res.setIsError(true);
                                res.setContent(new ArrayList<>());
                            }
                            return res;
                        });
                    }
                }
                mcpSyncServers.add(mcpSyncServer);
                log.info("MCP Start {}",aClass.getName());
            }
        }catch (Exception e){
            log.error(e.getMessage());
        }
    }

    @Override
    public void iocInitEnd() {
        hServerSseServerTransportProvider=new HServerSseServerTransportProvider();
        IocUtil.addBean(hServerSseServerTransportProvider);
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

        McpSchema.ServerCapabilities serverCapabilities = McpSchema.ServerCapabilities.builder()
                .tools(true)
                .resources(true, true)
                .prompts(true)
                .logging()
                .build();
        System.out.println(hServerSseServerTransportProvider);
        McpSyncServer build = McpServer.sync(hServerSseServerTransportProvider)
                .capabilities(serverCapabilities)
                .serverInfo("mcp-server", "1.0").build();
        McpServerFeatures.SyncToolSpecification syncToolSpecification = new McpServerFeatures.SyncToolSpecification();

        Map<String,String> data2=new HashMap<>();
        data2.put("type", "string");
        data2.put("description", "名字");
        String s ="";
        try {
             s = WebConstConfig.JSON.writeValueAsString(data2);
        }catch (Exception e){
            log.error(e.getMessage(),e);
        }
        System.out.println(s);

        McpSchema.JsonSchema jsonSchema = new McpSchema.JsonSchema();
        jsonSchema.setType("object");
 //* return ""inputSchema\":{\"type\":\"object\",\"properties\":{\"name\":{\"type\":\"string\",\"description\":\"名字\"}},\"required\":[\"name\"]}}]}}";

        Map<String,Object> data22=new HashMap<>();
        data22.put("name",data2);
        jsonSchema.setProperties(data22);
        List<String> datar=new ArrayList<>();
                datar.add("name");
        jsonSchema.setRequired(datar);

        McpSchema.Tool tool = new McpSchema.Tool();
        tool.setName("aaa");
        tool.setDescription("aaa");
        tool.setInputSchema(jsonSchema);
        syncToolSpecification.setTool(tool);
        syncToolSpecification.setCall((e,a)->{
            McpSchema.CallToolResult res = new McpSchema.CallToolResult();
            List<McpSchema.Content> data = new ArrayList<>();
            data.add(new McpSchema.TextContent("2222"));
            res.setContent(data);
            return res;
        });
        build.addTool(syncToolSpecification);
    }
}
