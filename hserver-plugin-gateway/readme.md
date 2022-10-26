# hserver-plugin-gateway
该插件提供tcp和http两种级别的数据拦截转发功能，让网关支持其他协议或者http协议

# BusinessTcp BusinessHttp7 BusinessHttp4
我们提供了这三个类，来进行代理的数据传输
tcp就是最原始的数据包 http7模式是对数据包进行编码解码操作用户更容易修改 http4是解析出sni host+原始数据包

我们通过继承类重写对应的方法即可

```java
    /**
    * 数据入场
    * 业务处理模式，拦截器 限流等
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
@Bean
public class Http7 extends BusinessHttp7 {

    private static final LongAdder online = new LongAdder();
    AttributeKey<String> ONLINE_KEY = AttributeKey.valueOf("online");

    @Override
    public Object in(ChannelHandlerContext ctx, Object msg) {
        System.out.println("通过" + msg.getClass() + "来判断是否进行解密或者统计操作");
        if (msg instanceof HttpRequest) {
            System.out.println("拦截修改等");
        }
        if (msg instanceof WebSocketFrame) {
            System.out.println("拦截修改等");
        }
        return msg;
    }

    @Override
    public SocketAddress getProxyHost(ChannelHandlerContext ctx, Object msg, SocketAddress sourceSocketAddress) {
        //在线数统计
        online.increment();
        ctx.channel().attr(ONLINE_KEY);

        System.out.println("通过" + msg.getClass() + "来判断是否进行对应负载");
        return new InetSocketAddress("127.0.0.1", 8081);
    }

    public Object out(Channel channel, Object msg) {
        System.out.println("通过" + msg.getClass() + "来判断是否进行加密操作");
        return msg;
    }

    @Override
    public void close(Channel channel) {
        if (channel.hasAttr(ONLINE_KEY)) {
            online.decrement();
            System.out.println("当前在线数：" + online);
        }
        super.close(channel);
    }
}

```