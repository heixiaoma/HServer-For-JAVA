package cn.hserver.core.server.exception;

import com.dyuproject.protostuff.runtime.MappedSchema;

import java.lang.reflect.Field;

/**
 * 验证异常类型
 * @author hxm
 */
public class ValidateException extends RuntimeException{

    private Field field;

    private Object data;
    public ValidateException(String message){
        super(message);
    }

    public ValidateException(String message, Field field,Object data){
        super(message);
        this.field=field;
        this.data=data;
    }

    public Field getField() {
        return field;
    }

    public void setField(Field field) {
        this.field = field;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
