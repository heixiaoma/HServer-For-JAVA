package cn.hserver.core.server.util;

import java.math.BigDecimal;
import java.math.BigInteger;

public class ObjConvertUtil {


    public static Object convert(Class<?> type, String res) {
        Object object = null;
        try {
            switch (type.getName()) {
                case "int":
                    if (res == null) {
                        object = 0;
                    } else {
                        object = Integer.parseInt(res);
                    }
                    break;
                case "java.lang.Integer":
                    object = Integer.parseInt(res);
                    break;

                case "double":
                    if (res == null) {
                        object = 0.0;
                    } else {
                        object = Double.parseDouble(res);
                    }
                    break;
                case "java.lang.Double":
                    object = Double.parseDouble(res);
                    break;

                case "long":
                    if (res == null) {
                        object = 0L;
                    } else {
                        object = Long.parseLong(res);
                    }
                    break;
                case "java.lang.Long":
                    object = Long.parseLong(res);
                    break;
                case "short":
                    if (res == null) {
                        object = 0;
                    } else {
                        object = Short.parseShort(res);
                    }
                    break;
                case "java.lang.Short":
                    object = Short.parseShort(res);
                    break;
                case "float":
                    if (res == null) {
                        object = 0;
                    } else {
                        object = Float.parseFloat(res);
                    }
                    break;
                case "java.lang.Float":
                    object = Float.parseFloat(res);
                    break;
                case "boolean":
                    if (res == null) {
                        object = false;
                    } else {
                        object = Boolean.parseBoolean(res);
                    }
                    break;
                case "java.lang.Boolean":
                    object = Boolean.parseBoolean(res);
                    break;
                case "byte":
                    if (res == null) {
                        object = false;
                    } else {
                        object = Byte.parseByte(res);
                    }
                    break;
                case "java.lang.Byte":
                    object = Byte.parseByte(res);
                    break;

                case "java.lang.BigInteger":
                    object = BigInteger.valueOf(Long.parseLong(res));
                    break;

                case "java.lang.BigDecimal":
                    object = BigDecimal.valueOf(Long.parseLong(res));
                    break;

                case "java.lang.String":
                    object = res;
                    break;
                default:
                    return null;
            }
        } catch (Exception ignored) {
        }
        return object;
    }
}
