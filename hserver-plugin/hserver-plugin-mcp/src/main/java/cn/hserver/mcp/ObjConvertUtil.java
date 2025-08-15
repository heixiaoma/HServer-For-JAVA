package cn.hserver.mcp;



import cn.hserver.core.util.Calculator;
import cn.hserver.mvc.constants.WebConstConfig;

import java.math.BigDecimal;
import java.math.BigInteger;

public class ObjConvertUtil {


    public static Object convert(Class<?> type, Object res) {
        Object object = null;
        try {
            switch (type.getName()) {
                case "int":
                    if (res == null) {
                        object = 0;
                    } else {
                        object = (int) Calculator.calculate(res.toString());
                    }
                    break;
                case "java.lang.Integer":
                    object =  (int)Calculator.calculate(res.toString());
                    break;

                case "double":
                    if (res == null) {
                        object = 0.0;
                    } else {
                        object = Calculator.calculate(res.toString());
                    }
                    break;
                case "java.lang.Double":
                    object = Calculator.calculate(res.toString());
                    break;

                case "long":
                    if (res == null) {
                        object = 0L;
                    } else {
                        object = (long)Calculator.calculate(res.toString());
                    }
                    break;
                case "java.lang.Long":
                    object =  (long)Calculator.calculate(res.toString());
                    break;
                case "short":
                    if (res == null) {
                        object = 0;
                    } else {
                        object = Short.parseShort(res.toString());
                    }
                    break;
                case "java.lang.Short":
                    object =  (short)Calculator.calculate(res.toString());
                    break;
                case "float":
                    if (res == null) {
                        object = 0;
                    } else {
                        object = (float)Calculator.calculate(res.toString());
                    }
                    break;
                case "java.lang.Float":
                    object = (float)Calculator.calculate(res.toString());
                    break;
                case "boolean":
                    if (res == null) {
                        object = false;
                    } else {
                        object = Boolean.parseBoolean(res.toString());
                    }
                    break;
                case "java.lang.Boolean":
                    object = Boolean.parseBoolean(res.toString());
                    break;
                case "byte":
                    if (res == null) {
                        object = false;
                    } else {
                        object = Byte.parseByte(res.toString());
                    }
                    break;
                case "java.lang.Byte":
                    object = Byte.parseByte(res.toString());
                    break;

                case "java.lang.BigInteger":
                    object = BigInteger.valueOf((long) Calculator.calculate(res.toString()));
                    break;

                case "java.lang.BigDecimal":
                    object = BigDecimal.valueOf(Calculator.calculate(res.toString()));
                    break;

                case "java.lang.String":
                    object = res;
                    break;
                default:
                    if (type.getSuperclass().isAssignableFrom(Enum.class)) {
                        object = Enum.valueOf((Class<? extends Enum>) type, res.toString());
                    } else {
                        object = WebConstConfig.JSONADAPTER.convertObjToObject(res, type);
                    }
            }
        } catch (Exception ignored) {
        }
        return object;
    }
}
