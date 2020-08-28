package top.hserver.cloud.client;

import com.alibaba.nacos.api.naming.listener.NamingEvent;
import com.alibaba.nacos.api.naming.pojo.Instance;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;
import top.hserver.cloud.bean.ServiceData;
import top.hserver.cloud.client.handler.RpcClientInitializer;
import top.hserver.cloud.client.handler.RpcServerHandler;
import top.hserver.cloud.util.DynamicRoundRobin;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static top.hserver.cloud.client.handler.RpcServerHandler.CLASS_STRING_MAP;

/**
 * @author hxm
 */
@Slf4j
public class NacosRpcClient {


    public static synchronized void  reconnect(ServiceData serviceData,String className){
        Instance instance = serviceData.getInstance();
            try {
                final EventLoopGroup group = new NioEventLoopGroup();
                Bootstrap b = new Bootstrap();
                b.group(group).channel(NioSocketChannel.class);
                b.handler(new RpcClientInitializer());
                //发起异步连接请求，绑定连接端口和host信息
                final ChannelFuture future = b.connect(instance.getIp(), instance.getPort()).sync();
                future.addListener((ChannelFutureListener) arg0 -> {
                    if (future.isSuccess()) {
                        log.debug("重新连接服务器成功");
                        DynamicRoundRobin serviceDataDynamicRoundRobin = CLASS_STRING_MAP.get(className);
                        if (serviceDataDynamicRoundRobin != null) {
                            serviceData.setChannel(future.channel());
                            serviceDataDynamicRoundRobin.add(serviceData);
                            CLASS_STRING_MAP.put(className,serviceDataDynamicRoundRobin);
                        }else {
                            future.channel().close();
                        }
                    } else {
                        log.debug("重新连接服务器失败");
                        future.cause().printStackTrace();
                        group.shutdownGracefully(); //关闭线程组
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
                log.error(e.getMessage());
            }
    }


    public static synchronized void connect(NamingEvent namingEvent) {
        String serviceName = namingEvent.getServiceName();
        //订阅的消息
        String[] split = serviceName.split("@@");
        String className =split[1];
        //移除之前的
        RpcServerHandler.closeChannel(className);

        for (Instance instance : namingEvent.getInstances()) {
            try {
                final EventLoopGroup group = new NioEventLoopGroup();
                Bootstrap b = new Bootstrap();
                b.group(group).channel(NioSocketChannel.class);
                b.handler(new RpcClientInitializer());
                //发起异步连接请求，绑定连接端口和host信息
                final ChannelFuture future = b.connect(instance.getIp(), instance.getPort()).sync();
                future.addListener((ChannelFutureListener) arg0 -> {
                    if (future.isSuccess()) {
                        log.debug("连接服务器成功");
                        DynamicRoundRobin serviceDataDynamicRoundRobin = CLASS_STRING_MAP.get(className);
                        if (serviceDataDynamicRoundRobin == null) {
                            serviceDataDynamicRoundRobin = new DynamicRoundRobin();
                        }
                        ServiceData serviceData = new ServiceData();
                        serviceData.setChannel(future.channel());
                        serviceData.setInstance(instance);
                        serviceData.setName(instance.getClusterName());
                        serviceDataDynamicRoundRobin.add(serviceData);
                        CLASS_STRING_MAP.put(className,serviceDataDynamicRoundRobin);
                    } else {
                        log.debug("连接服务器失败");
                        future.cause().printStackTrace();
                        group.shutdownGracefully(); //关闭线程组
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
                log.error(e.getMessage());
            }
        }
    }




}
