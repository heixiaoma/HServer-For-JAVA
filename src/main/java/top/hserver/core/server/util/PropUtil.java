package top.hserver.core.server.util;

import java.io.*;
import java.util.Properties;

import static top.hserver.core.server.context.ConstConfig.profiles;

/**
 * @author hxm
 */
public class PropUtil {

    private static PropUtil propUtil;

    private String name = "/application.properties";

    private Properties p = null;

    public PropUtil() {
    }

    public PropUtil(String fileName) {
        this.name = "/" + fileName;
    }

    public static PropUtil getInstance() {
        if (propUtil != null) {
            return propUtil;
        } else {
            propUtil = new PropUtil();
            return propUtil;
        }
    }

    private String getProFiles(String name) {
        return name == null ? null : "/application-" + name + ".properties";
    }

    private void initProp() {
        if (p == null) {
            p = new Properties();
            try (InputStream is = PropUtil.class.getResourceAsStream(name)) {
                p.load(is);
                //优先级查代码的，再查配置的
                if (profiles == null) {
                    profiles = p.getProperty("env");
                }
                if (profiles != null) {
                    try (InputStream is2 = PropUtil.class.getResourceAsStream(getProFiles(profiles))) {
                        Properties properties = new Properties();
                        properties.load(is2);
                        properties.forEach(p::put);
                    } catch (Exception ignored) {

                    }
                }
            } catch (Exception ignored) {
            }
        }
    }


    public String get(String key) {
        initProp();
        String property = p.getProperty(key);
        return property == null ? "" : property;
    }

    public String get(String key, String defaultValue) {
        String value = get(key.trim());
        if (isBlank(value)) {
            value = defaultValue;
        }
        return value.trim();
    }


    public Integer getInt(String key) {
        String s = get(key);
        if (s != null && s.trim().length() > 0) {
            try {
                return Integer.parseInt(s);
            } catch (Exception e) {
                return null;
            }
        }
        return null;
    }

    public Boolean getBoolean(String key) {
        String s = get(key);
        if (s != null && s.trim().length() > 0) {
            try {
                return Boolean.valueOf(s);
            } catch (Exception e) {
                return null;
            }
        }
        return null;
    }


    private boolean isBlank(String str) {
        int strLen;
        if (str != null && (strLen = str.length()) != 0) {
            for (int i = 0; i < strLen; ++i) {
                // 判断字符是否为空格、制表符、tab
                if (!Character.isWhitespace(str.charAt(i))) {
                    return false;
                }
            }
            return true;
        } else {
            return true;
        }
    }

}