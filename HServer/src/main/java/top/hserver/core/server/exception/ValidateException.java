package top.hserver.core.server.exception;

/**
 * 验证异常类型
 * @author hxm
 */
public class ValidateException extends RuntimeException{

    public ValidateException(String message){
        super(message);
    }

}
