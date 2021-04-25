package top.hserver.core.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.websocketx.*;
import io.netty.handler.codec.http.websocketx.extensions.compression.WebSocketClientCompressionHandler;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import top.hserver.core.ioc.IocUtil;
import top.hserver.core.ioc.annotation.WebSocketClient;

import java.net.URI;

/**
 * @author hxm
 */
public class HServerWebSocketClient extends Thread {

    @Override
    public void run() {
        if (HServerWebSocketClientHandler.beanName.size() > 0) {
            HServerWebSocketClientHandler.beanName.forEach(e -> {
                Object bean = IocUtil.getBean(e);
                if (bean != null) {
                    WebSocketClient annotation = bean.getClass().getAnnotation(WebSocketClient.class);
                    if (annotation != null) {
                        new Client(annotation.url(),e).start();
                    }
                }
            });

        }
    }

    private class Client extends Thread {

        private String url;

        private String name;

        public Client(String url, String name) {
            this.url = url;
            this.name = name;
        }

        @Override
        public void run() {
            try {
                URI uri = new URI(url);
                String scheme = uri.getScheme() == null ? "ws" : uri.getScheme();
                final String host = uri.getHost() == null ? "127.0.0.1" : uri.getHost();
                final int port;
                if (uri.getPort() == -1) {
                    if ("ws".equalsIgnoreCase(scheme)) {
                        port = 80;
                    } else if ("wss".equalsIgnoreCase(scheme)) {
                        port = 443;
                    } else {
                        port = -1;
                    }
                } else {
                    port = uri.getPort();
                }
                if (!"ws".equalsIgnoreCase(scheme) && !"wss".equalsIgnoreCase(scheme)) {
                    System.err.println("Only WS(S) is supported.");
                    return;
                }
                final boolean ssl = "wss".equalsIgnoreCase(scheme);
                final SslContext sslCtx;
                if (ssl) {
                    sslCtx = SslContextBuilder.forClient()
                            .trustManager(InsecureTrustManagerFactory.INSTANCE).build();
                } else {
                    sslCtx = null;
                }
                EventLoopGroup group = new NioEventLoopGroup();
                try {
                    final HServerWebSocketClientHandler handler =
                            new HServerWebSocketClientHandler(name,
                                    WebSocketClientHandshakerFactory.newHandshaker(
                                            uri, WebSocketVersion.V13, null, true, new DefaultHttpHeaders()));

                    Bootstrap b = new Bootstrap();
                    b.group(group)
                            .channel(NioSocketChannel.class)
                            .handler(new ChannelInitializer<SocketChannel>() {
                                @Override
                                protected void initChannel(SocketChannel ch) {
                                    ChannelPipeline p = ch.pipeline();
                                    if (sslCtx != null) {
                                        p.addLast(sslCtx.newHandler(ch.alloc(), host, port));
                                    }
                                    p.addLast(
                                            new HttpClientCodec(),
                                            new HttpObjectAggregator(Integer.MAX_VALUE),
                                            WebSocketClientCompressionHandler.INSTANCE,
                                            handler);
                                }
                            });
                    Channel channel = b.connect(uri.getHost(), port).sync().channel();
                    handler.handshakeFuture().sync();
                    channel.closeFuture().sync();
                } finally {
                    group.shutdownGracefully();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }
}
