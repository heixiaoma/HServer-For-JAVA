package cn.hserver.plugin.gateway.handler.http7.aggregator;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;
import io.netty.util.AttributeKey;

import java.util.List;


/**
 * 更具URL规则是否忽略消息聚合
 */
public class Http7UpObjectAggregator extends HttpObjectAggregator {

    private final static  AttributeKey<String> URI = AttributeKey.valueOf("URI");
    private final String igUrlRules;
    private final Channel channel;

    public Http7UpObjectAggregator(int maxContentLength, Channel channel, String igUrlRules) {
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
            // 根据具体的URL规则来判断是否不聚合消息
            if (uri.matches(igUrlRules)) {
                return false;
            }
        }
        return super.isStartMessage(msg);
    }
}
