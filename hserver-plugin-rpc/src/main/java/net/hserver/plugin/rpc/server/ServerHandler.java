package net.hserver.plugin.rpc.server;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import net.hserver.plugin.rpc.codec.InvokeServiceData;
import net.hserver.plugin.rpc.codec.Msg;
import net.hserver.plugin.rpc.codec.MsgType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServerHandler extends SimpleChannelInboundHandler<Msg> {
    private static final Logger log = LoggerFactory.getLogger(ServerHandler.class);

    public ServerHandler() {
    }

    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Msg msg) throws Exception {
        if (msg.getMsgType() == MsgType.INVOKER) {
            InvokeServiceData data = ((Msg<InvokeServiceData>) msg).getData();
            InvokerHandler.invoker(data, channelHandlerContext);
        }
    }

    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("断开异常信息：\r\n{}", cause.getMessage());
        ctx.close();
    }

    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {

        if (IdleStateEvent.class.isAssignableFrom(evt.getClass())) {
            IdleStateEvent event = (IdleStateEvent) evt;
            if (event.state() == IdleState.READER_IDLE) {
                log.debug("读空闲，关闭无用的连接");
                ctx.disconnect();
            } else if (event.state() == IdleState.WRITER_IDLE) {
//                log.debug("写空闲");
            } else if (event.state() == IdleState.ALL_IDLE) {
//                log.debug("读写都空闲");
            }
        }

    }


}
