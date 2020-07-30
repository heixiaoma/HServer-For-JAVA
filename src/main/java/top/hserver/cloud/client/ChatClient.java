package top.hserver.cloud.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;
import top.hserver.cloud.client.handler.FileServerInitializer;

/**
 * @author hxm
 */
@Slf4j
public class ChatClient extends Thread {

  private final String host;
  private final int port;
  public static Channel channel;

  //连接服务端的端口号地址和端口号
  public ChatClient(String host, int port) {
    this.host = host;
    this.port = port;
  }

  @Override
  public void run() {
    try {
      final EventLoopGroup group = new NioEventLoopGroup();
      Bootstrap b = new Bootstrap();
      b.group(group).channel(NioSocketChannel.class);
      b.handler(new FileServerInitializer());
      //发起异步连接请求，绑定连接端口和host信息
      final ChannelFuture future = b.connect(host, port).sync();

      future.addListener(new ChannelFutureListener() {
        @Override
        public void operationComplete(ChannelFuture arg0) throws Exception {
          if (future.isSuccess()) {
            log.debug("连接服务器成功");
          } else {
            log.debug("连接服务器失败");
            future.cause().printStackTrace();
            group.shutdownGracefully(); //关闭线程组
          }
        }
      });
      ChatClient.channel = future.channel();
    } catch (Exception e) {
      log.error(e.getMessage());
    }
  }
}
