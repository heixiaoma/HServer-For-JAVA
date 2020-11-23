package test8;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.util.ReferenceCountUtil;

import static test8.HConnection.POOL;

public class HClientHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        POOL.execute(() -> {
            try {
                if (msg instanceof FullHttpResponse) {
                    HFuture future = ChannelManager.attr(ctx.channel());
                    FullHttpResponse httpResponse = (FullHttpResponse) msg;
                    future.setHttpHeaders(httpResponse.headers());
                    future.setStatusCode(httpResponse.status().code());
                    future.write(httpResponse.content());
                    future.success();
                }
            } finally {
                ReferenceCountUtil.release(msg);
            }
        });
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        HFuture future = ChannelManager.attr(ctx.channel());
        future.error(cause);
    }

}