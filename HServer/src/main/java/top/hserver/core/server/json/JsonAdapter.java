package top.hserver.core.server.json;

/**
 * @author hxm
 */
public interface JsonAdapter {

    /**
     * string 转对象
     * @param data
     * @param type
     * @return
     */
    Object convertObject(String data,Class type);

    /**
     * 对象转String
     * @param data
     * @return
     */
    String convertString(Object data);


}
