package top.hserver.cloud.client.handler;

import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import top.hserver.cloud.CloudManager;
import top.hserver.cloud.bean.ClientData;
import top.hserver.cloud.bean.InvokeServiceData;
import top.hserver.cloud.bean.ResultData;
import top.hserver.cloud.common.MSG_TYPE;
import top.hserver.cloud.common.Msg;
import top.hserver.core.ioc.IocUtil;

import java.lang.reflect.Method;
import java.util.concurrent.CompletableFuture;


@Slf4j
public class InvokerHandler {


  public static Msg<ResultData> Invoker(InvokeServiceData data) {

    if (data != null) {
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
            resultData.setData(invoke);
            resultData.setUUID(data.getUUID());
            resultData.setCode(200);
            Msg<ResultData> msg2 = new Msg<>();
            msg2.setMsg_type(MSG_TYPE.RESULT);
            msg2.setData(resultData);
            return msg2;
          } catch (Exception e) {
            ResultData resultData = new ResultData();
            resultData.setData(e.getMessage());
            resultData.setUUID(data.getUUID());
            resultData.setCode(503);
            Msg<ResultData> msg2 = new Msg<>();
            msg2.setMsg_type(MSG_TYPE.RESULT);
            msg2.setData(resultData);
            return msg2;
          }
        }
      }
    }
    ResultData resultData = new ResultData();
    resultData.setData("空调用");
    resultData.setCode(503);
    Msg<ResultData> msg2 = new Msg<>();
    msg2.setMsg_type(MSG_TYPE.RESULT);
    msg2.setData(resultData);
    return msg2;
  }

  public static InvokeServiceData buildContext(ChannelHandlerContext ctx, Msg msg) {
    if (msg.getMsg_type() == MSG_TYPE.INVOKER) {
      InvokeServiceData data = ((Msg<InvokeServiceData>) msg).getData();
      return data;
    }
    return null;
  }

  public static Msg<ResultData> handleException(Throwable e) {
    ResultData resultData = new ResultData();
    resultData.setData(e.getMessage());
    resultData.setCode(503);
    Msg<ResultData> msg2 = new Msg<>();
    msg2.setMsg_type(MSG_TYPE.RESULT);
    msg2.setData(resultData);
    return msg2;
  }

  static void writeResponse(ChannelHandlerContext
                              ctx, CompletableFuture<Msg> future, Msg msg) {
    ctx.writeAndFlush(msg);
    future.complete(null);
  }
}
