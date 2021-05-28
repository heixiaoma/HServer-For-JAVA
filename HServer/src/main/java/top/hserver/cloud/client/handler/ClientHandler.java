package top.hserver.cloud.client.handler;


import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.hserver.cloud.bean.ResultData;
import top.hserver.cloud.common.MSG_TYPE;
import top.hserver.cloud.common.Msg;
import top.hserver.cloud.future.HFuture;
import top.hserver.cloud.future.RpcWrite;

import java.util.concurrent.CompletableFuture;


/**
 * @author hxm
 */
public class ClientHandler extends SimpleChannelInboundHandler<Msg<ResultData>> {

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Msg<ResultData> msg) throws Exception {
            if (msg.getMsg_type() == MSG_TYPE.RESULT) {
                ResultData resultData = msg.getData();
                String requestId = resultData.getRequestId();
                HFuture future = RpcWrite.syncKey.get(requestId);
                if (future!=null) {
                    future.setData(resultData);
                }
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
