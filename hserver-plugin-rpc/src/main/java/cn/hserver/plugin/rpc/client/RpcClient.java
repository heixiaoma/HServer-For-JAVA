package cn.hserver.plugin.rpc.client;

import cn.hserver.plugin.rpc.codec.*;
import cn.hserver.plugin.rpc.core.RpcDisHandler;
import cn.hserver.plugin.rpc.exception.RpcException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import cn.hserver.core.server.util.ExceptionUtil;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class RpcClient {
    private static final Logger log = LoggerFactory.getLogger(RpcClient.class);

    public static final Cache<String, CompletableFuture<?>> mapping = CacheBuilder.newBuilder().expireAfterAccess(5, TimeUnit.MINUTES).build();


    public static CompletableFuture<?> call(InvokeServiceData invokeServiceData) {
        try {
            ServiceData choose = RpcDisHandler.getRpcDisHandler().chose(invokeServiceData.getGroupName(), invokeServiceData.getServerName());
            if (choose == null) {
                throw new RpcException(invokeServiceData.getGroupName()+" 分组下暂无服务:" + invokeServiceData.getServerName());
            }
            ChannelPool channelPool = choose.getChannelPool();
            NettyChannel resource = channelPool.getResource();
            Msg<InvokeServiceData> msg = new Msg<>(MsgType.INVOKER);
            msg.setData(invokeServiceData);
            CompletableFuture<?> objectCompletableFuture = new CompletableFuture<>();
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
}
