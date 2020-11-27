package top.hserver.cloud.future;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import top.hserver.cloud.bean.InvokeServiceData;
import top.hserver.cloud.bean.ResultData;
import top.hserver.cloud.common.MSG_TYPE;
import top.hserver.cloud.common.Msg;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.*;

/**
 * @author hxm
 */
public class RpcWrite {

    public static Map<String, HFuture> syncKey = new ConcurrentHashMap<>();

    public static void writeAndSync(Channel channel, final InvokeServiceData invokeServiceData, HFuture future) throws Exception {
        if (channel == null) {
            throw new NullPointerException("channel");
        }
        if (invokeServiceData == null) {
            throw new NullPointerException("invokeServiceData");
        }
        //map里添加一个异步回调等待
        syncKey.put(invokeServiceData.getRequestId(), future);
        //开始远程调用等待
        doWriteAndSync(channel, invokeServiceData, future);
    }


    public static void removeKey(String reqId) {
        syncKey.remove(reqId);
    }


    private static void doWriteAndSync(Channel channel, final InvokeServiceData invokeServiceData, final HFuture writeFuture) throws Exception {
        Msg<InvokeServiceData> msg = new Msg<>();
        msg.setMsg_type(MSG_TYPE.INVOKER);
        msg.setData(invokeServiceData);
        channel.writeAndFlush(msg).addListener((ChannelFutureListener) future -> {
            if (!future.isSuccess()) {
                //不成功返回异常
                String requestId = invokeServiceData.getRequestId();
                ResultData resultData = new ResultData();
                resultData.setRequestId(requestId);
                resultData.setError(future.cause());
                writeFuture.setData(resultData);
            }
        });
    }

}
