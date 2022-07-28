package cn.hserver.plugin.web.exception;

/**
 * @author hxm
 */
public class MethodNotSupportException extends Exception {
    public MethodNotSupportException() {
        super("不支持当前请求方式");
    }

    public MethodNotSupportException(String s) {
        super(s);
    }
}
