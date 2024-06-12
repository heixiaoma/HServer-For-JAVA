package cn.hserver.plugin.gateway.handler.http7.aggregator;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;
import io.netty.util.AttributeKey;

import java.util.List;


/**
 * 更具URL规则是否忽略消息聚合
 */
public class Http7RequestObjectAggregator extends HttpObjectAggregator {

    private final static AttributeKey<String> URI = AttributeKey.valueOf("URI");
    private final String igUrlRules;
    private final Channel channel;

    public Http7RequestObjectAggregator(int maxContentLength, Channel channel, String igUrlRules) {
        super(maxContentLength);
        this.igUrlRules = igUrlRules;
        this.channel = channel;
    }


    @Override
    protected boolean isStartMessage(HttpObject msg) throws Exception {
        if (msg instanceof HttpRequest) {
            HttpRequest request = (HttpRequest) msg;
            String uri = request.uri();
            channel.attr(URI).set(uri);
            if (igUrlRules != null && uri.matches(igUrlRules)){
                return false;
            }
        }
        return super.isStartMessage(msg);
    }
}
