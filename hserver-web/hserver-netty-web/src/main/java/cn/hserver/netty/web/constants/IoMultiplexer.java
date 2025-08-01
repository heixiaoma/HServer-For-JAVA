package cn.hserver.netty.web.constants;

public enum IoMultiplexer {
	EPOLL, KQUEUE, JDK, IO_URING,DEFAULT
}