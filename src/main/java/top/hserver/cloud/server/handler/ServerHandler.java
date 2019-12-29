package top.hserver.cloud.server.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import top.hserver.cloud.CloudManager;
import top.hserver.cloud.bean.CloudData;
import top.hserver.cloud.bean.InvokeServiceData;
import top.hserver.cloud.bean.ResultData;
import top.hserver.cloud.bean.ServiceData;
import top.hserver.cloud.common.MSG_TYPE;
import top.hserver.cloud.common.Msg;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Slf4j
public class ServerHandler extends SimpleChannelInboundHandler<Msg> {

    private final static Map<String, ServiceData> classStringMap = new ConcurrentHashMap<>();

    private final static Map<String,Object> res=new ConcurrentHashMap<>();

    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Msg msg) throws Exception {

        switch (msg.getMsg_type()) {
            case REG:
                Msg<CloudData> cloudDataMsg = msg;
                CloudData data = cloudDataMsg.getData();
                ServiceData serviceData = new ServiceData();
                serviceData.setIp(data.getIp());
                serviceData.setName(data.getName());
                serviceData.setCtx(channelHandlerContext);
                data.getClasses().forEach(a -> classStringMap.put(a.getName(), serviceData));
                log.info(data.toString());
                break;
            case RESULT:
                Msg<ResultData> resultDataMsg=msg;
                ResultData data1 = resultDataMsg.getData();
                res.put(data1.getUUID(),data1.getData());
        }

    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
    }


    public static Object SendInvoker(InvokeServiceData invokeServiceData) throws TimeoutException {
        int timeout=500;
        Future<Object> submit = CloudManager.executorService.submit(() -> {
            ServiceData serviceData = classStringMap.get(invokeServiceData.getAClass());
            Msg<InvokeServiceData> msg = new Msg<>();
            msg.setMsg_type(MSG_TYPE.INVOKER);
            msg.setData(invokeServiceData);
            serviceData.getCtx().writeAndFlush(msg);
            //循环等待结果。
            long start=System.currentTimeMillis();
            while (true){
                if (System.currentTimeMillis()-start>timeout){
                    throw new TimeoutException("调用超时，请检测服务");
                }
                if (res.get(invokeServiceData.getUUID())!=null){
                    return res.get(invokeServiceData.getUUID());
                }
            }
        });
        try {
            Object o = submit.get(timeout, TimeUnit.MILLISECONDS);
            res.remove(invokeServiceData.getUUID());
            return o;
        } catch (Exception e) {
            return new TimeoutException("调用超时，请检测服务");
        }
    }

}
