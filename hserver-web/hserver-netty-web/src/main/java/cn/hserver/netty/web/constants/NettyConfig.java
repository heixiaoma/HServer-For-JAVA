package cn.hserver.netty.web.constants;


import io.netty.handler.ssl.SslContext;

public class NettyConfig {
    public static IoMultiplexer IO_MODE = IoMultiplexer.DEFAULT;

    /**
     * backlog 指定了内核为此套接口排队的最大连接个数；
     * 对于给定的监听套接口，内核要维护两个队列: 未连接队列和已连接队列
     * backlog 的值即为未连接队列和已连接队列的和。
     */
    public static Integer BACKLOG = 8192;

    public static Integer WORKER_POOL = 0;
    public static Long WRITE_LIMIT = null;
    public static Long READ_LIMIT = null;
    public static Integer HTTP_CONTENT_SIZE = Integer.MAX_VALUE;

    public static Integer MAX_WEBSOCKET_FRAME_LENGTH = 65535;
    public static SslContext SSL_CONTEXT;
    public static Integer SSL_PORT;

}
