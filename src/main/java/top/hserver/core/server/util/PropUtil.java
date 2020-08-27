package top.hserver.core.server.util;

import top.hserver.core.server.context.HeadMap;

import java.io.*;
import java.util.Properties;

import static top.hserver.core.server.context.ConstConfig.profiles;

/**
 * @author hxm
 */
public class PropUtil {

    private static PropUtil propUtil;

    private static HeadMap data = new HeadMap();

    private PropUtil() {
    }

    public static PropUtil getInstance() {
        if (propUtil != null) {
            return propUtil;
        } else {
            propUtil = new PropUtil();
            initProp();
            return propUtil;
        }
    }

    private static String getProFiles(String name) {
        return name == null ? null : "/app-" + name + ".properties";
    }

    private static void initProp() {
        Properties p = new Properties();
        try {
            String name = "/app.properties";
            InputStream is = PropUtil.class.getResourceAsStream(name);
            if (is == null) {
                return;
            }
            p.load(is);
            p.forEach((k, v) -> data.put(k.toString(), v.toString()));
            is.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        //优先级查代码的，再查配置的
        if (profiles == null) {
            profiles = data.get("env");
        }
        if (profiles != null) {
            try {
                InputStream is2 = PropUtil.class.getResourceAsStream(getProFiles(profiles));
                p.clear();
                if (is2 == null) {
                    return;
                }
                p.load(is2);
                p.forEach((k, v) -> data.put(k.toString(), v.toString()));
                is2.close();
                p.clear();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public String get(String key) {
        String property = data.get(key);
        return property == null ? "" : property;
    }

    public String get(String key, String defaultValue) {
        String value = get(key.trim());
        if (isBlank(value)) {
            value = defaultValue;
        }
        return value == null ? value : value.trim();
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