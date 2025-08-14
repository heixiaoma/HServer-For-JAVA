package cn.hserver.mcp;

import cn.hserver.core.ioc.annotation.Component;
import cn.hserver.mvc.constants.HttpMethod;
import cn.hserver.mvc.context.WebContext;
import cn.hserver.mvc.filter.FilterAdapter;

import static cn.hserver.mcp.McpPlugin.hServerSseServerTransportProviderList;

@Component
public class McpFilter implements FilterAdapter {

    @Override
    public void doFilter(WebContext webContext) throws Exception {
        if (hServerSseServerTransportProviderList.isEmpty()){
            return;
        }
        hServerSseServerTransportProviderList.forEach(provider -> {
            if (webContext.request.getRequestMethod() == HttpMethod.POST) {
                provider.doPost(webContext.request,webContext.response);
            }
            if (webContext.request.getRequestMethod() == HttpMethod.GET) {
                provider.doGet(webContext.request,webContext.response);
            }
        });
    }
}
