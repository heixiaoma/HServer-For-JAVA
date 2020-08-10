package top.hserver.cloud.future;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import top.hserver.cloud.bean.InvokeServiceData;
import top.hserver.cloud.bean.ResultData;
import top.hserver.cloud.common.MSG_TYPE;
import top.hserver.cloud.common.Msg;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class SyncWrite {

    public static Map<String, CompletableFuture<ResultData>> syncKey = new ConcurrentHashMap<>();

    public static ResultData writeAndSync(Channel channel, final InvokeServiceData invokeServiceData, final long timeout) throws Exception {
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

        //设置调用ID
        invokeServiceData.setRequestId(requestId);

        CompletableFuture<ResultData> future = CompletableFuture.completedFuture(new ResultData());
        //map里添加一个异步回调等待
        syncKey.put(invokeServiceData.getRequestId(), future);

        //开始远程调用等待
        ResultData response = doWriteAndSync(channel, invokeServiceData, timeout, future);
        //获取结果，删除原来的
        syncKey.remove(invokeServiceData.getRequestId());
        return response;
    }

    private static ResultData doWriteAndSync(Channel channel,final InvokeServiceData invokeServiceData, final long timeout, final CompletableFuture<ResultData> writeFuture) throws Exception {
        Msg<InvokeServiceData> msg = new Msg<>();
        msg.setMsg_type(MSG_TYPE.INVOKER);
        msg.setData(invokeServiceData);
        channel.writeAndFlush(msg).addListener((ChannelFutureListener) future -> writeFuture.thenApplyAsync(e->{
            if(!future.isSuccess()){
                e.setError(future.cause());
                syncKey.remove(e.getRequestId());
            }
            return null;
        }));



        ResultData resultData = writeFuture.get(timeout, TimeUnit.MILLISECONDS);
//        if (resultData == null) {
//            if (writeFuture.isTimeout()) {
//                throw new TimeoutException();
//            } else {
//                // write exception
//                throw new Exception(writeFuture.cause());
//            }
//        }
        return resultData;
    }

}
