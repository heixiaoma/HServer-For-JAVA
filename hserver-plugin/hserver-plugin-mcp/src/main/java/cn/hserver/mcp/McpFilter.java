package cn.hserver.mcp;

import cn.hserver.core.ioc.annotation.Autowired;
import cn.hserver.core.ioc.annotation.Bean;
import cn.hserver.modelcontextprotocol.server.transport.HServerSseServerTransportProvider;
import cn.hserver.plugin.web.context.Webkit;
import cn.hserver.plugin.web.interfaces.FilterAdapter;
import io.netty.handler.codec.http.HttpMethod;

@Bean
public class McpFilter implements FilterAdapter {

    @Autowired
    private HServerSseServerTransportProvider hServerSseServerTransportProvider;


    @Override
    public void doFilter(Webkit webkit) throws Exception {
        if (webkit.httpRequest.getRequestType() == HttpMethod.POST) {
            hServerSseServerTransportProvider.doPost(webkit.httpRequest,webkit.httpResponse);
        }
        if (webkit.httpRequest.getRequestType() == HttpMethod.GET) {
            hServerSseServerTransportProvider.doGet(webkit.httpRequest,webkit.httpResponse);
        }
    }

}
