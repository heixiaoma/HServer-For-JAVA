package cn.hserver.plugin.web.util;


import cn.hserver.plugin.web.annotation.validate.*;
import cn.hserver.plugin.web.exception.ValidateException;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
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
            Class<?>[] parameterTypes = method.getParameterTypes();
            for (Class<?> parameterType : parameterTypes) {
                Field[] declaredFields = parameterType.getDeclaredFields();
                for (Class aClass : validate) {
                    for (Field declaredField : declaredFields) {
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
     * 对外
     *
     * @param object
     * @throws ValidateException
     */
    public static void validate(Object object) throws ValidateException {
        if (object == null) {
            return;
        }
        try {
            Field[] fields = object.getClass().getDeclaredFields();
            for (int i = 0; i < fields.length; i++) {
                Field field = fields[i];
                field.setAccessible(true);
                Object obj = field.get(object);
                nullValidate(field, obj);
                notNull(field, obj);
                notEmpty(field, obj);
                notBlank(field, obj);
                assertFalse(field, obj);
                assertTrue(field, obj);
                lengthValidate(field, obj);
                max(field, obj);
                min(field, obj);
                pattern(field, obj);
                sizeValidate(field, obj);
            }
        } catch (Exception e) {
            throw new ValidateException(e.getMessage());
        }
    }

    /**
     * 字段为必须为 false
     *
     * @param field
     * @param obj
     * @throws ValidateException
     */
    private static void assertFalse(Field field, Object obj) throws ValidateException {
        AssertFalse assertFalse = field.getAnnotation(AssertFalse.class);
        if (assertFalse != null) {
            if (obj == null) {
                throw new ValidateException(assertFalse.message(), field, obj);
            }
            if (Boolean.parseBoolean(obj.toString())) {
                throw new ValidateException(assertFalse.message(), field, obj);
            }
        }
    }

    /**
     * 字段为必须为 true
     *
     * @param field
     * @param obj
     * @throws ValidateException
     */
    private static void assertTrue(Field field, Object obj) throws ValidateException {
        AssertTrue assertTrue = field.getAnnotation(AssertTrue.class);
        if (assertTrue != null) {
            if (obj == null) {
                throw new ValidateException(assertTrue.message(), field, obj);
            }
            if (!Boolean.parseBoolean(obj.toString())) {
                throw new ValidateException(assertTrue.message(), field, obj);
            }
        }
    }

    /**
     * 字段CharSequence 类型的长度必须是 length 长
     *
     * @param field
     * @param obj
     * @throws ValidateException
     */
    private static void lengthValidate(Field field, Object obj) throws ValidateException {
        Length length = field.getAnnotation(Length.class);
        if (length != null) {
            if (obj == null) {
                throw new ValidateException(length.message(), field, obj);
            }
            if (CharSequence.class.isAssignableFrom(field.getType())) {
                CharSequence obj1 = (CharSequence) obj;
                if (obj1.length() != length.value()) {
                    throw new ValidateException(length.message(), field, obj);
                }
            }
        }
    }


    /**
     * 字段值必须大于这个值，number
     *
     * @param field
     * @param obj
     * @throws ValidateException
     */
    private static void max(Field field, Object obj) throws ValidateException {
        Max max = field.getAnnotation(Max.class);
        if (max != null) {
            if (obj == null || Long.parseLong(obj.toString()) < max.value()) {
                throw new ValidateException(max.message(), field, obj);
            }
        }
    }

    /**
     * 字段值必须小于这个值，number
     *
     * @param field
     * @param obj
     * @throws ValidateException
     */
    private static void min(Field field, Object obj) throws ValidateException {
        Min min = field.getAnnotation(Min.class);
        if (min != null) {
            if (obj == null || Long.parseLong(obj.toString()) > min.value()) {
                throw new ValidateException(min.message(), field, obj);
            }
        }
    }

    /**
     * 字段不能为null同时不是 ""
     *
     * @param field
     * @param obj
     * @throws ValidateException
     */
    private static void notBlank(Field field, Object obj) throws ValidateException {
        NotBlank notBlank = field.getAnnotation(NotBlank.class);
        if (notBlank != null) {
            if (obj == null || obj.toString().trim().length() == 0) {
                throw new ValidateException(notBlank.message(), field, obj);
            }
        }

    }

    /**
     * CharSequence 集合 map 数组 不是null 长度或者size 大于0
     *
     * @param field
     * @param obj
     * @throws ValidateException
     */
    private static void notEmpty(Field field, Object obj) throws ValidateException {
        NotEmpty notEmpty = field.getAnnotation(NotEmpty.class);
        if (notEmpty != null) {
            if (obj == null) {
                throw new ValidateException(notEmpty.message(), field, obj);
            }
            //CharSequence
            if (CharSequence.class.isAssignableFrom(field.getType())) {
                if (((CharSequence) obj).length() == 0) {
                    throw new ValidateException(notEmpty.message(), field, obj);
                }
            }
            //集合
            if (Collection.class.isAssignableFrom(field.getType())) {
                if (((Collection) obj).size() == 0) {
                    throw new ValidateException(notEmpty.message(), field, obj);
                }
            }
            //Map
            if (Map.class.isAssignableFrom(field.getType())) {
                if (((Map) obj).size() == 0) {
                    throw new ValidateException(notEmpty.message(), field, obj);
                }
            }
            //数组类型
            if (field.getType().isArray()) {
                if (((Object[]) obj).length == 0) {
                    throw new ValidateException(notEmpty.message(), field, obj);
                }
            }
        }
    }

    /**
     * 字段不能为Null
     *
     * @param field
     * @param obj
     * @throws ValidateException
     */
    private static void notNull(Field field, Object obj) throws ValidateException {
        NotNull notNull = field.getAnnotation(NotNull.class);
        if (notNull != null) {
            if (obj == null) {
                throw new ValidateException(notNull.message(), field, obj);
            }
        }
    }

    /**
     * 字段必须为Null
     *
     * @param field
     * @param obj
     * @throws ValidateException
     */
    private static void nullValidate(Field field, Object obj) throws ValidateException {
        Null isNull = field.getAnnotation(Null.class);
        if (isNull != null) {
            if (obj != null) {
                throw new ValidateException(isNull.message(), field, obj);
            }
        }
    }

    /**
     * 字段CharSequence 必须满足这个正则
     *
     * @param field
     * @param obj
     * @throws ValidateException
     */
    private static void pattern(Field field, Object obj) throws ValidateException {
        Pattern pattern = field.getAnnotation(Pattern.class);
        if (pattern != null) {
            if (obj == null) {
                throw new ValidateException(pattern.message(), field, obj);
            }
            if (CharSequence.class.isAssignableFrom(field.getType())) {
                if (!java.util.regex.Pattern.matches(pattern.value(), obj.toString())) {
                    throw new ValidateException(pattern.message(), field, obj);
                }
            }
        }
    }

    /**
     * 字段 CharSequence 集合 map 数组必须在这范围内
     *
     * @param field
     * @param obj
     * @throws ValidateException
     */
    private static void sizeValidate(Field field, Object obj) throws ValidateException {

        Size size = field.getAnnotation(Size.class);
        if (size != null) {
            if (obj == null) {
                throw new ValidateException(size.message(), field, obj);
            }

            /**
             * min=1
             * max=3
             * length=2
             *
             */

            //CharSequence
            if (CharSequence.class.isAssignableFrom(field.getType())) {
                int length = ((CharSequence) obj).length();
                if (length < size.min() || length > size.max()) {
                    throw new ValidateException(size.message(), field, obj);

                }
            }
            //集合
            if (Collection.class.isAssignableFrom(field.getType())) {
                int length = ((Collection) obj).size();
                if (length < size.min() || length > size.max()) {
                    throw new ValidateException(size.message(), field, obj);


                }
            }
            //Map
            if (Map.class.isAssignableFrom(field.getType())) {
                int length = ((Map) obj).size();
                if (length < size.min() || length > size.max()) {
                    throw new ValidateException(size.message(), field, obj);

                }
            }
            //数组类型
            if (field.getType().isArray()) {
                assert obj instanceof Object[];
                int length = ((Object[]) obj).length;
                if (length < size.min() || length > size.max()) {
                    throw new ValidateException(size.message(), field, obj);

                }
            }
        }
    }
}
