package net.hserver.plugin.rpc.client;

import net.hserver.plugin.rpc.bean.RpcServer;
import net.hserver.plugin.rpc.codec.*;
import net.hserver.plugin.rpc.exception.RpcException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import net.hserver.core.server.util.ExceptionUtil;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

public class RpcClient {
    private static final Logger log = LoggerFactory.getLogger(RpcClient.class);

    private final static Map<String, DynamicRoundRobin> S_DATA = new ConcurrentHashMap<>();

    public static Map<String, CompletableFuture> mapping = new ConcurrentHashMap<>();

    public static CompletableFuture call(InvokeServiceData invokeServiceData) {
        try {
            DynamicRoundRobin dynamicRoundRobin = S_DATA.get(invokeServiceData.getServerName());
            if (dynamicRoundRobin == null) {
                throw new RpcException("暂无服务:" + invokeServiceData.getServerName());
            }
            ServiceData choose = dynamicRoundRobin.choose();
            if (choose == null) {
                throw new RpcException("暂无服务:" + invokeServiceData.getServerName());
            }
            ChannelPool channelPool = choose.getChannelPool();
            NettyChannel resource = channelPool.getResource();
            Msg<InvokeServiceData> msg = new Msg<>(MsgType.INVOKER);
            msg.setData(invokeServiceData);
            CompletableFuture objectCompletableFuture = new CompletableFuture<>();
            mapping.put(invokeServiceData.getRequestId(), objectCompletableFuture);
            if (!resource.getCh().isActive()) {
                throw new RpcException("RPC连接异常:" + invokeServiceData.getServerName());
            }
            resource.getCh().writeAndFlush(msg);
            channelPool.returnResource(resource);
            return objectCompletableFuture;
        } catch (Exception e) {
            throw new RpcException("调用异常:" + ExceptionUtil.getMessage(e));
        }
    }

    public static void remove(RpcServer rpcServer) {
        DynamicRoundRobin dynamicRoundRobin = S_DATA.get(rpcServer.getServerName());
        if (dynamicRoundRobin!=null) {
            List<ServiceData> all = dynamicRoundRobin.getAll();
            for (ServiceData serviceData : all) {
                serviceData.closeChannelPool();
            }
            S_DATA.remove(rpcServer.getServerName());
        }
    }

    public static void reg(RpcServer rpcServer) {
        log.debug("服务{}添加到client的池子里",rpcServer);
        ServiceData serviceData = new ServiceData();
        serviceData.setServerName(rpcServer.getServerName());
        serviceData.setPort(rpcServer.getPort());
        serviceData.setIp(rpcServer.getIp());
        DynamicRoundRobin dynamicRoundRobin = S_DATA.get(serviceData.getServerName());
        if (dynamicRoundRobin != null) {
            dynamicRoundRobin.add(serviceData);
        } else {
            dynamicRoundRobin = new DynamicRoundRobin();
            dynamicRoundRobin.add(serviceData);
            S_DATA.put(serviceData.getServerName(), dynamicRoundRobin);
        }
        //初始化连接
        serviceData.initChannelPool();
    }
}
