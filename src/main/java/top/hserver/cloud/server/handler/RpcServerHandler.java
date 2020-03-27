package top.hserver.cloud.server.handler;

import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import top.hserver.cloud.bean.CloudData;
import top.hserver.cloud.bean.InvokeServiceData;
import top.hserver.cloud.bean.ResultData;
import top.hserver.cloud.bean.ServiceData;
import top.hserver.cloud.common.Msg;
import top.hserver.cloud.future.SyncWrite;
import top.hserver.cloud.future.SyncWriteFuture;
import top.hserver.cloud.future.SyncWriteMap;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class RpcServerHandler {

  private final static Map<String, ServiceData> classStringMap = new ConcurrentHashMap<>();



  public static InvokeServiceData readData(ChannelHandlerContext ctx, Msg msg) {
    switch (msg.getMsg_type()) {
      case REG:
        CloudData data = ((Msg<CloudData>) msg).getData();
        ServiceData serviceData = new ServiceData();
        serviceData.setIp(data.getIp());
        serviceData.setName(data.getName());
        serviceData.setCtx(ctx);
        data.getClasses().forEach(a -> {
          classStringMap.put(a, serviceData);
        });
        log.debug(data.toString());
        break;
      case RESULT:
        ResultData resultData = ((Msg<ResultData>) msg).getData();
        String requestId = resultData.getUUID();
        SyncWriteFuture future = (SyncWriteFuture) SyncWriteMap.syncKey.get(requestId);
        if (future != null) {
          future.setResultData(resultData);
        }
    }
    return null;
  }



  public static Object SendInvoker(InvokeServiceData invokeServiceData) throws Exception {
    ServiceData serviceData = classStringMap.get(invokeServiceData.getAClass());

    if (serviceData != null) {
      ChannelHandlerContext ctx = serviceData.getCtx();
      if (ctx != null && ctx.channel().isActive()) {
        ResultData response = new SyncWrite().writeAndSync(ctx, invokeServiceData, 5000);
        switch (response.getCode()) {
          case 200:
            return response.getData();
          case 404:
            return new NullPointerException("暂无服务");
          default:
            return new NullPointerException("远程调用异常");
        }
      } else {
        return new NullPointerException("暂无服务");
      }
    } else {
      return new NullPointerException("暂无服务");
    }
  }

}
