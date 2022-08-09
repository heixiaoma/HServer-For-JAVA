package cn.hserver.core.server.util.protocol;

public enum HttpProtocol {

    /**
     * 不知道该协议。
     */
    UNKNOWN("unknown"),


    HTTP_1_0("HTTP/1.0"),


    HTTP_1_1("HTTP/1.1"),

    /**
     * Chromium 的二进制帧协议，包括标头压缩、多路复用
     * 同一套接字上的请求和服务器推送。 HTTP 1.1 语义在 SPDY 3 上分层。
     */
    SPDY_3("spdy/3.1"),

    /**
     * IETF 的二进制帧协议，包括报头压缩、多路复用
     * 同一套接字上的请求和服务器推送。 HTTP1.1 语义在 HTTP2 上分层。
     */
    HTTP_2("h2"),

    /**
     * 没有“升级”往返的明文 HTTP2。此选项要求客户事先
     * 知道服务器支持明文 HTTP2
     */
    H2_PRIOR_KNOWLEDGE("h2_prior_knowledge"),

    /**
     * QUIC（快速 UDP Internet 连接）是一种新的基于 UDP 的多路复用和安全传输，
     * 从头开始设计并针对 HTTP2 语义进行了优化。
     * HTTP/1.1 semantics are layered on HTTP/2.
     */
    QUIC("quic");

    private final String protocol;

    HttpProtocol(String protocol) {
        this.protocol = protocol;
    }


    /**
     * 转换
     * @param protocol
     * @return
     */
    public static HttpProtocol parse(String protocol) {
        if (protocol.equalsIgnoreCase(HTTP_1_0.protocol)) {
            return HTTP_1_0;
        } else if (protocol.equalsIgnoreCase(HTTP_1_1.protocol)) {
            return HTTP_1_1;
        } else if (protocol.equalsIgnoreCase(H2_PRIOR_KNOWLEDGE.protocol)) {
            return H2_PRIOR_KNOWLEDGE;
        } else if (protocol.equalsIgnoreCase(HTTP_2.protocol)) {
            return HTTP_2;
        } else if (protocol.equalsIgnoreCase(SPDY_3.protocol)) {
            return SPDY_3;
        } else if (protocol.equalsIgnoreCase(QUIC.protocol)) {
            return QUIC;
        } else {
            return UNKNOWN;
        }
    }


    @Override
    public String toString() {
        return this.protocol;
    }

}