package top.hserver.cloud.future;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import top.hserver.cloud.bean.InvokeServiceData;
import top.hserver.cloud.bean.ResultData;
import top.hserver.cloud.common.MSG_TYPE;
import top.hserver.cloud.common.Msg;

import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;


public class SyncWrite {

    public ResultData writeAndSync(ChannelHandlerContext channel, final InvokeServiceData invokeServiceData, final long timeout) throws Exception {

        if (channel == null) {
            throw new NullPointerException("channel");
        }
        if (invokeServiceData == null) {
            throw new NullPointerException("invokeServiceData");
        }
        if (timeout <= 0) {
            throw new IllegalArgumentException("timeout <= 0");
        }

        String requestId = UUID.randomUUID().toString();
        invokeServiceData.setUUID(requestId);

        WriteFuture<ResultData> future = new SyncWriteFuture(invokeServiceData.getUUID());
        SyncWriteMap.syncKey.put(invokeServiceData.getUUID(), future);

        ResultData response = doWriteAndSync(channel, invokeServiceData, timeout, future);

        SyncWriteMap.syncKey.remove(invokeServiceData.getUUID());
        return response;
    }

    private ResultData doWriteAndSync(ChannelHandlerContext channel, final InvokeServiceData invokeServiceData, final long timeout, final WriteFuture<ResultData> writeFuture) throws Exception {
        Msg<InvokeServiceData> msg = new Msg<>();
        msg.setMsg_type(MSG_TYPE.INVOKER);
        msg.setData(invokeServiceData);

        channel.writeAndFlush(msg).addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) {
                writeFuture.setWriteResult(future.isSuccess());
                writeFuture.setCause(future.cause());
                //失败移除
                if (!writeFuture.isWriteSuccess()) {
                    SyncWriteMap.syncKey.remove(writeFuture.requestId());
                }
            }
        });

        ResultData resultData = writeFuture.get(timeout, TimeUnit.MILLISECONDS);
        if (resultData == null) {
            if (writeFuture.isTimeout()) {
                throw new TimeoutException();
            } else {
                // write exception
                throw new Exception(writeFuture.cause());
            }
        }
        return resultData;
    }

}
