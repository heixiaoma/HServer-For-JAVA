package top.hserver.core.server.exception;

/**
 * @author hxm
 */
public class RpcException extends Exception {

    public RpcException() {
        super();
    }

    public RpcException(String s) {
        super(s);
    }
    public RpcException(Throwable throwable) {
        super(throwable);
    }
}
