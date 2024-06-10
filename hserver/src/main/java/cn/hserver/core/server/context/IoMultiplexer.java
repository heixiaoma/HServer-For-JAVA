package cn.hserver.core.server.context;

public enum IoMultiplexer {
	EPOLL, KQUEUE, JDK, IO_URING,DEFAULT
}