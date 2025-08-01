package cn.hserver.mvc.json;

import java.lang.reflect.Parameter;
import java.util.Map;

/**
 * @author hxm
 */
public interface JsonAdapter {

    /**
     * string 转对象
     *
     * @param data
     * @param type
     * @return
     */
    Object convertObject(String data, Class type);


    /**
     * 参数类型转换
     *
     * @param data
     * @param type
     * @return
     */
    Object convertObject(String data, Parameter type);

    /**
     * map转对象
     *
     * @param data
     * @param type
     * @return
     */
    Object convertMapToObject(Map data, Class type);

    /**
     * obj转换
     * @param data
     * @param type
     * @return
     */
    Object convertObjToObject(Object data, Class type);

    /**
     * 对象转String
     *
     * @param data
     * @return
     */
    String convertString(Object data);


}
