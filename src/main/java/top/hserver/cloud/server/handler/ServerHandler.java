package top.hserver.cloud.server.handler;

import com.alibaba.fastjson.JSON;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import top.hserver.cloud.bean.CloudData;
import top.hserver.cloud.bean.InvokeServiceData;
import top.hserver.cloud.bean.ServiceData;
import top.hserver.cloud.common.MSG_TYPE;
import top.hserver.cloud.common.Msg;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class ServerHandler extends SimpleChannelInboundHandler<Msg> {

    private final static Map<String,ServiceData> classStringMap=new ConcurrentHashMap<>();

    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Msg msg) throws Exception {

        switch (msg.getMsg_type()){
            case REG:
                Msg<CloudData> cloudDataMsg=msg;
                CloudData data = cloudDataMsg.getData();
                ServiceData serviceData=new ServiceData();
                serviceData.setIp(data.getIp());
                serviceData.setName(data.getName());
                serviceData.setCtx(channelHandlerContext);
                data.getClasses().forEach(a->classStringMap.put(a.getName(),serviceData));
                log.info(data.toString());
                break ;
            case RESULT:
                log.info(msg.toString());
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


    public static void SendInvoker(InvokeServiceData invokeServiceData){
        ServiceData serviceData = classStringMap.get(invokeServiceData.getAClass());
        Msg<InvokeServiceData> msg=new Msg<>();
        msg.setMsg_type(MSG_TYPE.INVOKER);
        msg.setData(invokeServiceData);
        serviceData.getCtx().writeAndFlush(msg);
    }

}
