# hserver-plugin-gateway
数据流级别代理该插件提供tcp和http两种级别的数据拦截转发功能，让网关支持其他协议或者http协议

# BusinessTcp BusinessHttp7 BusinessHttp4
我们提供了这三个类，来进行代理的数据传输
tcp就是最原始的数据包 http7模式是对数据包进行编码解码操作用户更容易修改 http4是解析出sni host+原始数据包



我们通过继承类重写对应的方法即可

```java
    /**
 * 数据入场
 * 业务处理模式，拦截器 限流 中断请求 异常返回 等
 *
 * @param t
 * @return
 */
    Object in(ChannelHandlerContext ctx,T t);


            /**
             * 代理host
             * 业务代码只处理选择什么样的服务，比如常见随机模式 hash模式 循环模式等
             *
             * @param t
             * @param sourceSocketAddress
             * @return
             */
            SocketAddress getProxyHost(ChannelHandlerContext ctx,T t,SocketAddress sourceSocketAddress);


            /**
             * 数据出场
             * 数据加密等操作
             *
             * @param u
             * @return
             */
            Object out(Channel channel, U u);


            /**
             * 链接关闭
             *
             * @param channel
             */
            void close(Channel channel);
```


## 举栗子
```java

import cn.hserver.core.ioc.annotation.Bean;
import cn.hserver.plugin.gateway.business.BusinessHttp7;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.util.AttributeKey;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.LongAdder;

import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

@Bean
public class Http7 extends BusinessHttp7 {

    private static final LongAdder online = new LongAdder();
    AttributeKey<String> ONLINE_KEY = AttributeKey.valueOf("online");

    @Override
    public Object in(ChannelHandlerContext ctx, Object msg) {
        System.out.println("通过" + msg.getClass() + "来判断是否进行解密或者统计操作");
        //网关拦截
        if (msg instanceof HttpRequest) {
            HttpRequest msg1 = (HttpRequest) msg;
            if (msg1.uri().contains("/ccc")) {
                ctx.writeAndFlush(getFullHttpResponse("<html><body><h1>错误页面网关拦截</h1></body></html>", HttpResponseStatus.BAD_GATEWAY));
                return null;
            }
        }

        //修改http请求
        if (msg instanceof HttpRequest) {
            HttpRequest msg1 = (HttpRequest) msg;
            msg1.headers().set("gateway", "xxxx");
            msg = msg1;
        }
        //修改ws数据
        if (msg instanceof WebSocketFrame) {
            if (msg instanceof TextWebSocketFrame) {
                TextWebSocketFrame msg1 = (TextWebSocketFrame) msg;
                msg = new TextWebSocketFrame("拦截修改：" + msg1.text());
            }
            System.out.println("拦截修改等");
        }
        return msg;
    }

    @Override
    public SocketAddress getProxyHost(ChannelHandlerContext ctx, Object msg, SocketAddress sourceSocketAddress) {
        //在线数统计
        online.increment();
        ctx.channel().attr(ONLINE_KEY);
        System.out.println("当前在线数：" + online);
        System.out.println("通过" + msg.getClass() + "来判断是否进行对应负载");
        return new InetSocketAddress("127.0.0.1", 8081);
    }

    public Object out(Channel channel, Object msg) {
        System.out.println("通过" + msg.getClass() + "来判断是否进行加密操作");
        //http响应拦截修改
        if (msg instanceof FullHttpResponse) {
            FullHttpResponse msg1 = (FullHttpResponse) msg;
            msg1.headers().set("gateway", "hserver-gateway");
        }
        //ws 响应拦截修改
        if (msg instanceof WebSocketFrame) {
            if (msg instanceof TextWebSocketFrame) {
                TextWebSocketFrame msg1 = (TextWebSocketFrame) msg;
                msg = new TextWebSocketFrame("拦截修改：" + msg1.text());
            }
        }
        return msg;
    }

    @Override
    public void close(Channel channel) {
        if (channel.hasAttr(ONLINE_KEY)) {
            online.decrement();
        }
        super.close(channel);
    }


    private  FullHttpResponse getFullHttpResponse(String html, HttpResponseStatus httpResponseStatus) {
        FullHttpResponse response = new DefaultFullHttpResponse(
                HTTP_1_1,
                httpResponseStatus,
                Unpooled.wrappedBuffer(html.getBytes(StandardCharsets.UTF_8)));
        response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/html;charset=UTF-8");
        response.headers().set(HttpHeaderNames.CONTENT_LENGTH, response.content().readableBytes());
        response.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
        return response;
    }

}


```

- 针对http7有一个消息编码忽略，如果url匹配者忽略消息的编码合并，通常在处理下载任务时，需要忽略，不然数据会全合并在内存，导致内存占用忒高，影响性能。

```java

/**
 * 忽略请求，但是不忽略响应，通常是上传文件
 */
@Override
public String upIgnoreUrls() {
        return "/upload*";
}

/**
 * 忽略响应，但是不忽略请求，通常是上下载文件
 */
@Override
public String downIgnoreUrls() {
        return "/down*";
}

```
