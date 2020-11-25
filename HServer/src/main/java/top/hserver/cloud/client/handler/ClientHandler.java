package top.hserver.cloud.client.handler;


import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import top.hserver.cloud.bean.ResultData;
import top.hserver.cloud.common.MSG_TYPE;
import top.hserver.cloud.common.Msg;
import top.hserver.cloud.future.RpcWrite;

import java.util.concurrent.CompletableFuture;


/**
 * @author hxm
 */
@Slf4j
public class ClientHandler extends SimpleChannelInboundHandler<Msg> {

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Msg msg) throws Exception {
        if (msg.getMsg_type() == MSG_TYPE.RESULT) {
            ResultData resultData = ((Msg<ResultData>) msg).getData();
            String requestId = resultData.getRequestId();
            CompletableFuture<ResultData> future = RpcWrite.syncKey.get(requestId);
            future.complete(resultData);
        }
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
