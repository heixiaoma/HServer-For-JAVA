package cn.hserver.mvc.util;



import cn.hserver.mvc.annotation.validate.*;
import cn.hserver.mvc.exception.ValidateException;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Collection;
import java.util.Map;

/**
 * validate注解验证工具类
 *
 * @author hxm
 */
public class ValidateUtil {

    private final static Class[] validate = new Class[]{
            Null.class, NotNull.class, NotEmpty.class, NotBlank.class,
            AssertFalse.class, AssertTrue.class, Length.class, Max.class, Min.class,
            Pattern.class, Size.class
    };

    public static boolean isValidate(Method method) {
        if (method != null) {
            Parameter[] parameters = method.getParameters();
            for (Parameter parameterType : parameters) {
                Field[] declaredFields = parameterType.getType().getDeclaredFields();
                for (Class aClass : validate) {
                    for (Field declaredField : declaredFields) {
                        //参数是否有注解
                        if (parameterType.getAnnotation(aClass) != null) {
                            return true;
                        }
                        //字段上是否有注解
                        if (declaredField.getAnnotation(aClass) != null) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }


    /**
     * 验证参数
     * @param object
     * @param parameter
     * @throws ValidateException
     */
    public static void validate(Object object,Parameter parameter) throws ValidateException {
        if (parameter == null) {
            return;
        }

        nullValidate(parameter.getAnnotation(Null.class), object,parameter.getType());
        notNull(parameter.getAnnotation(NotNull.class), object,parameter.getType());
        notEmpty(parameter.getAnnotation(NotEmpty.class), object,parameter.getType());
        notBlank(parameter.getAnnotation(NotBlank.class), object,parameter.getType());
        assertFalse(parameter.getAnnotation(AssertFalse.class), object,parameter.getType());
        assertTrue(parameter.getAnnotation(AssertTrue.class), object,parameter.getType());
        lengthValidate(parameter.getAnnotation(Length.class), object,parameter.getType());
        max(parameter.getAnnotation(Max.class), object,parameter.getType());
        min(parameter.getAnnotation(Min.class), object,parameter.getType());
        pattern(parameter.getAnnotation(Pattern.class), object,parameter.getType());
        sizeValidate(parameter.getAnnotation(Size.class), object,parameter.getType());

        /**
         * 检查字段的注解
         */
        if (object == null) {
            return;
        }
        try {
            Field[] fields = object.getClass().getDeclaredFields();
            for (Field field : fields) {
                field.setAccessible(true);
                Object obj = field.get(object);
                Class<?> type = field.getType();
                nullValidate(field.getAnnotation(Null.class), obj,type);
                notNull(field.getAnnotation(NotNull.class), obj,type);
                notEmpty(field.getAnnotation(NotEmpty.class), obj,type);
                notBlank(field.getAnnotation(NotBlank.class), obj,type);
                assertFalse(field.getAnnotation(AssertFalse.class), obj,type);
                assertTrue(field.getAnnotation(AssertTrue.class), obj,type);
                lengthValidate(field.getAnnotation(Length.class), obj,type);
                max(field.getAnnotation(Max.class), obj,type);
                min(field.getAnnotation(Min.class), obj,type);
                pattern(field.getAnnotation(Pattern.class), obj,type);
                sizeValidate(field.getAnnotation(Size.class), obj,type);
            }
        } catch (Exception e) {
            throw new ValidateException(e.getMessage());
        }
    }


    /**
     * 字段为必须为 false
     *
     * @param assertFalse
     * @param obj
     * @param type
     * @throws ValidateException
     */
    private static void assertFalse(AssertFalse assertFalse, Object obj,Class<?> type) throws ValidateException {
        if (assertFalse != null) {
            if (obj == null) {
                throw new ValidateException(assertFalse.message(), type);
            }
            if (Boolean.parseBoolean(obj.toString())) {
                throw new ValidateException(assertFalse.message(), type, obj);
            }
        }
    }

    /**
     * 字段为必须为 true
     * @param assertTrue
     * @param obj
     * @param type
     * @throws ValidateException
     */
    private static void assertTrue(AssertTrue assertTrue, Object obj,Class<?> type) throws ValidateException {
        if (assertTrue != null) {
            if (obj == null) {
                throw new ValidateException(assertTrue.message(), type);
            }
            if (!Boolean.parseBoolean(obj.toString())) {
                throw new ValidateException(assertTrue.message(),type, obj);
            }
        }
    }

    /**
     * 字段CharSequence 类型的长度必须是 length 长
     * @param length
     * @param obj
     * @param type
     * @throws ValidateException
     */
    private static void lengthValidate(Length length, Object obj,Class<?> type) throws ValidateException {
        if (length != null) {
            if (obj == null) {
                throw new ValidateException(length.message(), type);
            }
            if (CharSequence.class.isAssignableFrom(type)) {
                CharSequence obj1 = (CharSequence) obj;
                if (obj1.length() != length.value()) {
                    throw new ValidateException(length.message(),type, obj);
                }
            }
        }
    }


    /**
     * 字段值必须小于这个值，number
     * @param max
     * @param obj
     * @param type
     * @throws ValidateException
     */
    private static void max( Max max, Object obj,Class<?> type) throws ValidateException {
        if (max != null) {
            if (obj == null || Long.parseLong(obj.toString()) < max.value()) {
                throw new ValidateException(max.message(), type, obj);
            }
        }
    }


    /**
     * 字段值必须大于这个值，number
     * @param min
     * @param obj
     * @param type
     * @throws ValidateException
     */
    private static void min(Min min, Object obj,Class<?> type) throws ValidateException {
        if (min != null) {
            if (obj == null || Long.parseLong(obj.toString()) > min.value()) {
                throw new ValidateException(min.message(),type, obj);
            }
        }
    }

    /**
     * 字段不能为null同时不是 ""
     * @param notBlank
     * @param obj
     * @param type
     * @throws ValidateException
     */
    private static void notBlank(NotBlank notBlank, Object obj,Class<?> type) throws ValidateException {
        if (notBlank != null) {
            if (obj == null || obj.toString().trim().isEmpty()) {
                throw new ValidateException(notBlank.message(), type, obj);
            }
        }

    }

    /**
     * 字段不能为null同时不是 "" 集合 map 数组 不是null 长度或者size 大于0
     * @param notEmpty
     * @param obj
     * @param type
     * @throws ValidateException
     */
    private static void notEmpty(NotEmpty notEmpty, Object obj,Class<?> type) throws ValidateException {
        if (notEmpty != null) {
            if (obj == null) {
                throw new ValidateException(notEmpty.message(), type);
            }
            //CharSequence
            if (CharSequence.class.isAssignableFrom(type)) {
                if (((CharSequence) obj).length() == 0) {
                    throw new ValidateException(notEmpty.message(), type, obj);
                }
            }
            //集合
            if (Collection.class.isAssignableFrom(type)) {
                if (((Collection<?>) obj).isEmpty()) {
                    throw new ValidateException(notEmpty.message(), type, obj);
                }
            }
            //Map
            if (Map.class.isAssignableFrom(type)) {
                if (((Map<?, ?>) obj).isEmpty()) {
                    throw new ValidateException(notEmpty.message(), type, obj);
                }
            }
            //数组类型
            if (type.isArray()) {
                int length = Array.getLength(obj);
                if (length== 0) {
                    throw new ValidateException(notEmpty.message(), type, obj);
                }
            }
        }
    }


    /**
     * 字段不能为null
     * @param notNull
     * @param obj
     * @param type
     * @throws ValidateException
     */
    private static void notNull( NotNull notNull, Object obj,Class<?> type) throws ValidateException {
        if (notNull != null) {
            if (obj == null) {
                throw new ValidateException(notNull.message(), type);
            }
        }
    }


    /**
     * 字段必须为null
     * @param isNull
     * @param obj
     * @param type
     * @throws ValidateException
     */
    private static void nullValidate(Null isNull, Object obj,Class<?> type) throws ValidateException {
        if (isNull != null) {
            if (obj != null) {
                throw new ValidateException(isNull.message(),type, obj);
            }
        }
    }

    /**
     * 字段CharSequence 必须满足这个正则
     * @param pattern
     * @param obj
     * @param type
     * @throws ValidateException
     */
    private static void pattern(Pattern pattern , Object obj,Class<?> type) throws ValidateException {
        if (pattern != null) {
            if (obj == null) {
                throw new ValidateException(pattern.message(), type);
            }
            if (CharSequence.class.isAssignableFrom(type)) {
                if (!java.util.regex.Pattern.matches(pattern.value(), obj.toString())) {
                    throw new ValidateException(pattern.message(), type, obj);
                }
            }
        }
    }

    /**
     * 字段 CharSequence 集合 map 数组必须在这范围内
     * @param size
     * @param obj
     * @param type
     * @throws ValidateException
     */
    private static void sizeValidate(Size size, Object obj,Class<?> type) throws ValidateException {
        if (size != null) {
            if (obj == null) {
                throw new ValidateException(size.message(), type);
            }
            /**
             * min=1
             * max=3
             * length=2
             *
             */

            //CharSequence
            if (CharSequence.class.isAssignableFrom(type)) {
                int length = ((CharSequence) obj).length();
                if (length < size.min() || length > size.max()) {
                    throw new ValidateException(size.message(),type, obj);

                }
            }
            //集合
            if (Collection.class.isAssignableFrom(type)) {
                int length = ((Collection<?>) obj).size();
                if (length < size.min() || length > size.max()) {
                    throw new ValidateException(size.message(), type, obj);


                }
            }
            //Map
            if (Map.class.isAssignableFrom(type)) {
                int length = ((Map<?, ?>) obj).size();
                if (length < size.min() || length > size.max()) {
                    throw new ValidateException(size.message(),type, obj);

                }
            }
            //数组类型
            if (type.isArray()) {
                int length = Array.getLength(obj);
                if (length < size.min() || length > size.max()) {
                    throw new ValidateException(size.message(), type, obj);
                }
            }
        }
    }
}
