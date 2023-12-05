package cn.hserver.plugin.gateway.handler.http7;

import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequest;

/**
 * 更具URL规则是否忽略消息聚合
 */
public class Http7ObjectAggregator extends HttpObjectAggregator {
    private final String igUrlRules;

    public Http7ObjectAggregator(int maxContentLength, String igUrlRules) {
        super(maxContentLength);
        this.igUrlRules = igUrlRules;
    }

    @Override
    protected boolean isStartMessage(HttpObject msg) throws Exception {
        if (igUrlRules != null && msg instanceof HttpRequest) {
            HttpRequest request = (HttpRequest) msg;
            String uri = request.uri();
            // 根据具体的URL规则来判断是否不聚合消息
            if (uri.matches(igUrlRules)) {
                return false;
            }
        }
        return super.isStartMessage(msg);
    }
}
