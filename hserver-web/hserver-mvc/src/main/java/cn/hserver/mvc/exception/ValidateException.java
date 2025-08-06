package cn.hserver.mvc.exception;

/**
 * 验证异常类型
 * @author hxm
 */
public class ValidateException extends RuntimeException{
    private final Class<?> type;
    private final Object data;
    public ValidateException(String message){
        super(message);
        this.type=null;
        this.data=null;
    }

    public ValidateException(String message, Class<?> type,Object data){
        super(message);
        this.type=type;
        this.data=data;
    }
    public ValidateException(String message, Class<?> type){
        super(message);
        this.type=type;
        this.data=null;
    }



    public Class<?> getType() {
        return type;
    }

    public Object getData() {
        return data;
    }
}
