package top.hserver.core.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
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
                        new Client(annotation.url(), e).start();
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
            EventLoopGroup group = new NioEventLoopGroup();
            Bootstrap b = new Bootstrap();
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
                 HServerWebSocketClientHandler handler =
                        new HServerWebSocketClientHandler(name,
                                WebSocketClientHandshakerFactory.newHandshaker(
                                        uri, WebSocketVersion.V13, null, true, new DefaultHttpHeaders()));

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
                new ImConnection(uri.getHost(), port, b, handler).doConnect();
            } catch (Exception e) {

            }
        }
    }

    public static class ImConnection {
        private String host;
        private Integer port;
        private Bootstrap b;
        private HServerWebSocketClientHandler handler;

        public ImConnection(String host, Integer port, Bootstrap b, HServerWebSocketClientHandler handler) {
            this.host = host;
            this.port = port;
            this.b = b;
            this.handler = handler;
        }

        public void doConnect() {
            try {
                handler.setImConnection(this);
                Channel channel = b.connect(host, port).addListener(new ConnectionListener(this)).sync().channel();
                handler.handshakeFuture().sync();
                channel.closeFuture().sync();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    public static class ConnectionListener implements ChannelFutureListener {

        private ImConnection imConnection;

        public ConnectionListener(ImConnection imConnection) {
            this.imConnection = imConnection;
        }

        @Override
        public void operationComplete(ChannelFuture channelFuture) throws Exception {
            if (!channelFuture.isSuccess()) {
                try {
                    Thread.sleep(1000);
                    imConnection.doConnect();
                } catch (Exception e) {

                }
            }
        }
    }


}
