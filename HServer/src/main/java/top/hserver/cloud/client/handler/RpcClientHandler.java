package top.hserver.cloud.client.handler;

import io.netty.channel.Channel;
import io.netty.channel.pool.SimpleChannelPool;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.FutureListener;
import top.hserver.cloud.bean.InvokeServiceData;
import top.hserver.cloud.bean.ResultData;
import top.hserver.cloud.bean.ServiceData;
import top.hserver.cloud.client.RpcClient;
import top.hserver.cloud.future.HFuture;
import top.hserver.cloud.future.RpcWrite;
import top.hserver.cloud.util.DynamicRoundRobin;
import top.hserver.core.server.context.ConstConfig;
import top.hserver.core.server.exception.RpcException;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * @author hxm
 */
public class RpcClientHandler {

    /**
     * 服务名，List<服务集群host和IP>
     */
    private final static Map<String, DynamicRoundRobin> S_DATA = new ConcurrentHashMap<>();

    public static void reg(ServiceData serviceData) {
        DynamicRoundRobin dynamicRoundRobin = S_DATA.get(serviceData.getServerName());
        if (dynamicRoundRobin != null) {
            dynamicRoundRobin.add(serviceData);
        } else {
            dynamicRoundRobin = new DynamicRoundRobin();
            dynamicRoundRobin.add(serviceData);
            S_DATA.put(serviceData.getServerName(), dynamicRoundRobin);
        }
    }

    public static Map<String, DynamicRoundRobin> getAll() {
        return S_DATA;
    }


    public static void clear(String serverName) {
        S_DATA.remove(serverName);
    }


    public static Object sendInvoker(InvokeServiceData invokeServiceData) throws Exception {
        DynamicRoundRobin dynamicRoundRobin = S_DATA.get(invokeServiceData.getServerName());
        if (dynamicRoundRobin != null) {
            ServiceData serviceData = dynamicRoundRobin.choose();
            if (serviceData != null) {
                HFuture hFuture = new HFuture();
                SimpleChannelPool pool = RpcClient.channels.get(serviceData.getInetSocketAddress());
                Future<Channel> acquire = pool.acquire();
                acquire.addListener((FutureListener<Channel>) future -> {
                    //给服务端发送数据
                    Channel channel = future.getNow();
                    RpcWrite.writeAndSync(channel, invokeServiceData, hFuture);
                    // 连接放回连接池，这里一定记得放回去
                    pool.release(channel);
                });
                try {
                    ResultData response = hFuture.get(ConstConfig.rpcTimeOut, TimeUnit.MILLISECONDS);
                    if (response.getCode().code() == 200) {
                        return response.getData();
                    }
                    if (response.getError() != null) {
                        throw new RpcException(response.getError());
                    } else {
                        throw new RpcException("远程调用异常");
                    }
                } catch (Exception e) {
                    throw new RpcException(e.getMessage());
                } finally {
                    RpcWrite.removeKey(invokeServiceData.getRequestId());
                }
            }
        }
        throw new RpcException("暂无服务");
    }
}
