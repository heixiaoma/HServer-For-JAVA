package top.hserver.cloud.client.handler;


import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import top.hserver.cloud.CloudManager;
import top.hserver.cloud.bean.ClientData;
import top.hserver.cloud.bean.InvokeServiceData;
import top.hserver.cloud.bean.ResultData;
import top.hserver.cloud.common.MSG_TYPE;
import top.hserver.cloud.common.Msg;
import top.hserver.core.ioc.IocUtil;

import java.lang.reflect.Method;


@Slf4j
public class ClientHandler extends SimpleChannelInboundHandler<Msg> {

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Msg msg) throws Exception {
        switch (msg.getMsg_type()) {
            case INVOKER:
                Msg<InvokeServiceData> msg1 = msg;
                InvokeServiceData data = msg1.getData();
                log.info("调用信息--->" + data.toString());
                //返回调用结果
                String aClass = data.getAClass();
                ClientData clientData = CloudManager.get(aClass);
                Object bean = IocUtil.getBean(aClass);
                for (Method method : clientData.getMethods()) {
                    if (method.getName().equals(data.getMethod())){
                        try {
                            Object invoke = method.invoke(bean, data.getObjects());
                            ResultData<String> resultData = new ResultData<>();
                            resultData.setData(invoke.toString());
                            resultData.setUUID(data.getUUID());
                            resultData.setCode(200);
                            Msg<ResultData> msg2 = new Msg<>();
                            msg2.setMsg_type(MSG_TYPE.RESULT);
                            msg2.setData(resultData);
                            channelHandlerContext.writeAndFlush(msg2);
                            break;
                        }catch (Exception e){
                            ResultData<String> resultData = new ResultData<>();
                            resultData.setData(e.getMessage());
                            resultData.setUUID(data.getUUID());
                            resultData.setCode(503);
                            Msg<ResultData> msg2 = new Msg<>();
                            msg2.setMsg_type(MSG_TYPE.RESULT);
                            msg2.setData(resultData);
                            channelHandlerContext.writeAndFlush(msg2);
                            break;
                        }
                    }
                }
                break;
            default:
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

}
