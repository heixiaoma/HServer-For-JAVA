package top.hserver.cloud.server.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import top.hserver.cloud.bean.*;
import top.hserver.cloud.common.Msg;
import top.hserver.cloud.future.SyncWrite;
import top.hserver.cloud.future.SyncWriteFuture;
import top.hserver.cloud.future.SyncWriteMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class ServerHandler extends SimpleChannelInboundHandler<Msg> {

    private final static Map<String, ServiceData> classStringMap = new ConcurrentHashMap<>();

    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Msg msg) throws Exception {

        switch (msg.getMsg_type()) {
            case REG:
                CloudData data = ((Msg<CloudData>) msg).getData();
                ServiceData serviceData = new ServiceData();
                serviceData.setIp(data.getIp());
                serviceData.setName(data.getName());
                serviceData.setCtx(channelHandlerContext);
                data.getClasses().forEach(a -> {
                    classStringMap.put(a.getName(), serviceData);
                });
                log.info(data.toString());
                break;
            case RESULT:
                ResultData resultData = ((Msg<ResultData>) msg).getData();
                String requestId = resultData.getUUID();
                SyncWriteFuture future = (SyncWriteFuture) SyncWriteMap.syncKey.get(requestId);
                if (future != null) {
                    future.setResultData(resultData);
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

    public static Object SendInvoker(InvokeServiceData invokeServiceData) throws Exception {
        ServiceData serviceData = classStringMap.get(invokeServiceData.getAClass());
        ResultData response = new SyncWrite().writeAndSync(serviceData.getCtx(), invokeServiceData, 5000);
        return response.getData();
    }
}
