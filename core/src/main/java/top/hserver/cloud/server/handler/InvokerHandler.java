package top.hserver.cloud.server.handler;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.HttpResponseStatus;
import lombok.extern.slf4j.Slf4j;
import top.hserver.cloud.CloudManager;
import top.hserver.cloud.bean.ClientData;
import top.hserver.cloud.bean.InvokeServiceData;
import top.hserver.cloud.bean.ResultData;
import top.hserver.cloud.common.MSG_TYPE;
import top.hserver.cloud.common.Msg;
import top.hserver.core.ioc.IocUtil;

import java.lang.reflect.Method;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * @author hxm
 */
@Slf4j
public class InvokerHandler {


    public static final Set<Channel> CONSUMER_CHANNEL = new CopyOnWriteArraySet<>();

    static void invoker(InvokeServiceData data, ChannelHandlerContext ctx) {
        if (data != null && !data.isPingPing()) {
            log.debug("调用信息--->{}", data.toString());
            //返回调用结果
            String aClass = data.getAClass();
            ClientData clientData = CloudManager.get(aClass);
            Object bean = IocUtil.getBean(aClass);
            for (Method method : clientData.getMethods()) {
                if (method.getName().equals(data.getMethod())) {
                    try {
                        Object invoke = method.invoke(bean, data.getObjects());
                        ResultData resultData = new ResultData();
                        resultData.setCode(HttpResponseStatus.OK);
                        resultData.setRequestId(data.getRequestId());
                        Msg<ResultData> msg2 = new Msg<>();
                        msg2.setMsg_type(MSG_TYPE.RESULT);
                        resultData.setData(invoke);
                        msg2.setData(resultData);
                        ctx.writeAndFlush(msg2);
                    } catch (Throwable e) {
                        ResultData resultData = new ResultData();
                        resultData.setError(e);
                        resultData.setRequestId(data.getRequestId());
                        resultData.setCode(HttpResponseStatus.INTERNAL_SERVER_ERROR);
                        Msg<ResultData> msg2 = new Msg<>();
                        msg2.setMsg_type(MSG_TYPE.RESULT);
                        msg2.setData(resultData);
                        ctx.writeAndFlush(msg2);
                    }
                }
            }
        } else if (data != null) {
            ResultData resultData = new ResultData();
            resultData.setData("ok");
            resultData.setCode(HttpResponseStatus.INTERNAL_SERVER_ERROR);
            Msg<ResultData> msg2 = new Msg<>();
            msg2.setMsg_type(MSG_TYPE.PINGPONG);
            msg2.setData(resultData);
            ctx.writeAndFlush(msg2);
        } else {
            ResultData resultData = new ResultData();
            resultData.setData("空调用");
            resultData.setCode(HttpResponseStatus.INTERNAL_SERVER_ERROR);
            Msg<ResultData> msg2 = new Msg<>();
            msg2.setMsg_type(MSG_TYPE.RESULT);
            msg2.setData(resultData);
            ctx.writeAndFlush(msg2);
        }
    }

    static InvokeServiceData buildContext(ChannelHandlerContext ctx, Msg msg) {
        if (msg.getMsg_type() == MSG_TYPE.INVOKER) {
            InvokeServiceData data = ((Msg<InvokeServiceData>) msg).getData();
            return data;
        } else if (msg.getMsg_type() == MSG_TYPE.PINGPONG) {
            //存储一个ctx
            CONSUMER_CHANNEL.add(ctx.channel());
            log.debug("ping-pong");
            InvokeServiceData invokeServiceData = new InvokeServiceData();
            invokeServiceData.setPingPing(true);
            return invokeServiceData;
        }
        return null;
    }
}
