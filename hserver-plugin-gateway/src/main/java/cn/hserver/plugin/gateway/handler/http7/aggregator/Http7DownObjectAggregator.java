package cn.hserver.plugin.gateway.handler.http7.aggregator;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;
import io.netty.util.AttributeKey;

import java.util.List;


/**
 * 更具URL规则是否忽略消息聚合
 */
public class Http7DownObjectAggregator extends HttpObjectAggregator {

    private final static AttributeKey<String> URI = AttributeKey.valueOf("URI");
    private final String igUrlRules;
    private final Channel channel;

    public Http7DownObjectAggregator(int maxContentLength, Channel channel, Boolean isReq, String igUrlRules) {
        super(maxContentLength);
        this.igUrlRules = igUrlRules;
        this.channel = channel;
    }


    @Override
    protected boolean isStartMessage(HttpObject msg) throws Exception {
        if (igUrlRules != null && msg instanceof HttpResponse) {
            if (channel.hasAttr(URI)) {
                String uri = channel.attr(URI).getAndSet(null);
                // 根据具体的URL规则来判断是否不聚合消息
                if (uri != null && uri.matches(igUrlRules)) {
                    return false;
                }
            }
        }
        return super.isStartMessage(msg);
    }
}
