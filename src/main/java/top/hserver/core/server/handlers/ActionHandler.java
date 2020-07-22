package top.hserver.core.server.handlers;

import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.util.ReferenceCountUtil;
import top.hserver.core.server.context.HServerContext;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;


/**
 * @author hxm
 */
@Slf4j
public class ActionHandler extends SimpleChannelInboundHandler<HServerContext> {

    @Override
    public void channelRead0(ChannelHandlerContext ctx, HServerContext hServerContext) throws Exception {
        try {
            DispatcherHandler.staticFile(hServerContext);
            DispatcherHandler.permission(hServerContext);
            DispatcherHandler.filter(hServerContext);
            DispatcherHandler.findController(hServerContext);
            FullHttpResponse fullHttpResponse = DispatcherHandler.buildResponse(hServerContext);
            DispatcherHandler.writeResponse(ctx,fullHttpResponse);
        }catch (Throwable e){
            FullHttpResponse fullHttpResponse = DispatcherHandler.handleException(e);
            DispatcherHandler.writeResponse(ctx,fullHttpResponse);

        }finally {
            ReferenceCountUtil.release(hServerContext);
        }
    }


    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
      BuildResponse.writeException(ctx,cause);
    }
}