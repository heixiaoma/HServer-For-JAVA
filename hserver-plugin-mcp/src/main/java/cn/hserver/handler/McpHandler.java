package cn.hserver.handler;

import cn.hserver.msg.ReqMsg;
import cn.hserver.plugin.web.context.sse.SSeStream;

public class McpHandler extends MsgHandler {

    public McpHandler(SSeStream sSeStream) {
        super(sSeStream);
    }

    @Override
    protected String toolsCall(ReqMsg reqMsg) {
        return "{\"jsonrpc\":\"2.0\",\"id\":"+getId()+",\"result\":{\"content\":[{\"type\":\"text\",\"text\":\"hello 是是是\"}],\"isError\":false}}";
    }

    @Override
    protected String toolsList(ReqMsg reqMsg) {
        return "{\"jsonrpc\":\"2.0\",\"id\":"+getId()+",\"result\":{\"tools\":[{\"name\":\"hello\",\"description\":\"你好世界\",\"inputSchema\":{\"type\":\"object\",\"properties\":{\"name\":{\"type\":\"string\",\"description\":\"名字\"}},\"required\":[\"name\"]}}]}}";
    }

    @Override
    protected String initialize(ReqMsg reqMsg) {
        return "{\"jsonrpc\":\"2.0\",\"id\":"+getId()+",\"result\":{\"protocolVersion\":\"2024-11-05\",\"capabilities\":{\"logging\":{},\"tools\":{\"listChanged\":false}},\"serverInfo\":{\"name\":\"HelloService\",\"version\":\"1.0.0\"}}}";
    }
}
