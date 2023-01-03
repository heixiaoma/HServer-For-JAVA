package cn.hserver.plugin.rpc.server;

import io.netty.channel.ChannelHandlerContext;
import cn.hserver.plugin.rpc.codec.*;
import cn.hserver.core.ioc.IocUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.concurrent.CompletableFuture;

public class InvokerHandler {

    private static final Logger log = LoggerFactory.getLogger(InvokerHandler.class);

    static void invoker(InvokeServiceData data, ChannelHandlerContext ctx) {
        if (data != null) {
            String aClass = data.getaClass();
            Object bean = IocUtil.getBean(aClass);
            try {
                Method method = bean.getClass().getMethod(data.getMethod(), data.getParameterTypes());
                method.setAccessible(true);
                final Object invoke = method.invoke(bean, data.getObjects());
                if (!(invoke instanceof CompletableFuture)){
                    log.error("接口返回类型必须定义为 CompletableFuture 类型");
                    throw new RuntimeException(String.format("返回类型错误，你设定的：%s，要求类型为：CompletableFuture",invoke.getClass().getTypeName()));
                }
                ResultData resultData = new ResultData();
                resultData.setCode(MsgCode.SUCCESS);
                resultData.setRequestId(data.getRequestId());
                Msg<ResultData> msg2 = new Msg<>();
                msg2.setMsgType(MsgType.RESULT);
                resultData.setData(invoke);
                msg2.setData(resultData);
                ctx.writeAndFlush(msg2);
            } catch (Throwable e) {
                log.error(e.getMessage(),e);
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