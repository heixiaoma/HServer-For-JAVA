package top.hserver.core.server.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.hserver.core.server.HServer;
import top.hserver.core.server.context.ConstConfig;
import top.hserver.core.server.context.HeadMap;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.Properties;

import static top.hserver.core.server.context.ConstConfig.profiles;

/**
 * @author hxm
 */
public class PropUtil {
    private static final Logger log = LoggerFactory.getLogger(PropUtil.class);

    private static PropUtil propUtil;

    private static final HeadMap data = new HeadMap();

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
            InputStreamReader is = getFileStream(name);
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
                InputStreamReader is2 = getFileStream(getProFiles(profiles));
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


    private static InputStreamReader getFileStream(String path) {
        //先检查外部文件，在检查内部文件，外部优先级最高
        String rootPath = System.getProperty("user.dir");
        try {
            return new InputStreamReader(new FileInputStream(rootPath + path), StandardCharsets.UTF_8);
        } catch (Exception e) {
            return new InputStreamReader(Objects.requireNonNull(PropUtil.class.getResourceAsStream(path)), StandardCharsets.UTF_8);
        }
    }

}