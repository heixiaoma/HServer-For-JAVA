package cn.hserver.netty.web.handler;

import cn.hserver.core.context.IocApplicationContext;
import cn.hserver.mvc.annotation.WebSocket;
import cn.hserver.mvc.websoket.WebSocketHandler;
import cn.hserver.netty.web.constants.NettyConfig;
import cn.hserver.netty.web.handler.websocket.WebSocketServerHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.handler.traffic.GlobalTrafficShapingHandler;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class NettyServerHandler extends ChannelInitializer<SocketChannel> {

    private  GlobalTrafficShapingHandler globalTrafficShapingHandler;

    public static final Map<String, WebSocketHandler> WEB_SOCKET_ROUTER = new ConcurrentHashMap<>();

    public NettyServerHandler(){
        List<WebSocketHandler> beansOfType = IocApplicationContext.getBeansOfType(WebSocketHandler.class);
        beansOfType.forEach(handler -> {
            Class<? extends WebSocketHandler> aClass = handler.getClass();
            WebSocket annotation = aClass.getAnnotation(WebSocket.class);
            if(annotation != null){
                WEB_SOCKET_ROUTER.put(annotation.value(),handler);
            }
        });
    }


    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        ChannelPipeline pipeline = socketChannel.pipeline();
        if (NettyConfig.WRITE_LIMIT != null && NettyConfig.READ_LIMIT != null) {
            if (globalTrafficShapingHandler == null) {
                globalTrafficShapingHandler = new GlobalTrafficShapingHandler( socketChannel.eventLoop(), NettyConfig.WRITE_LIMIT, NettyConfig.READ_LIMIT);
            }
            pipeline.addLast( globalTrafficShapingHandler);
        }
        pipeline.addLast(new HttpServerCodec());
        pipeline.addLast(new HttpObjectAggregator(NettyConfig.HTTP_CONTENT_SIZE));
        pipeline.addLast(new ChunkedWriteHandler());
        //有websocket才走他
        if (!WEB_SOCKET_ROUTER.isEmpty()) {
            pipeline.addLast(new WebSocketServerHandler());
        }
        pipeline.addLast(HServerContentHandler.getInstance());
    }
}
