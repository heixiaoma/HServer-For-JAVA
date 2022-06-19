package net.hserver.plugin.rpc.server;

import io.netty.channel.ChannelHandlerContext;
import net.hserver.plugin.rpc.codec.*;
import net.hserver.core.ioc.IocUtil;

import java.lang.reflect.Method;
import java.util.concurrent.CompletableFuture;

public class InvokerHandler {

    static void invoker(InvokeServiceData data, ChannelHandlerContext ctx) {
        if (data != null) {
            String aClass = data.getaClass();
            Object bean = IocUtil.getBean(aClass);
            try {
                Method method = bean.getClass().getMethod(data.getMethod(), data.getParameterTypes());
                method.setAccessible(true);
                CompletableFuture invoke = (CompletableFuture) method.invoke(bean, data.getObjects());
                ResultData resultData = new ResultData();
                resultData.setCode(MsgCode.SUCCESS);
                resultData.setRequestId(data.getRequestId());
                Msg<ResultData> msg2 = new Msg<>();
                msg2.setMsgType(MsgType.RESULT);
                resultData.setData(invoke);
                msg2.setData(resultData);
                ctx.writeAndFlush(msg2);
            } catch (Throwable e) {
                e.printStackTrace();
                ResultData resultData = new ResultData();
                resultData.setError(e);
                resultData.setRequestId(data.getRequestId());
                resultData.setCode(MsgCode.ERROR);
                Msg<ResultData> msg2 = new Msg<>();
                msg2.setMsgType(MsgType.RESULT);
                msg2.setData(resultData);
                ctx.writeAndFlush(msg2);
            }
        } else {
            ResultData resultData = new ResultData();
            resultData.setData("空调用");
            resultData.setCode(MsgCode.ERROR);
            Msg<ResultData> msg2 = new Msg<>();
            msg2.setMsgType(MsgType.RESULT);
            msg2.setData(resultData);
            ctx.writeAndFlush(msg2);
        }
    }

}