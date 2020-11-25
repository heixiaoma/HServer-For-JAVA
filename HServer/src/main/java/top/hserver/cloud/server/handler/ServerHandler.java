package top.hserver.cloud.server.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import top.hserver.cloud.bean.InvokeServiceData;
import top.hserver.cloud.common.Msg;

@Slf4j
public class ServerHandler extends SimpleChannelInboundHandler<Msg> {


    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Msg msg) throws Exception {
        InvokeServiceData invokeServiceData = InvokerHandler.buildContext(channelHandlerContext, msg);
        InvokerHandler.invoker(invokeServiceData,channelHandlerContext);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }

}
