package cn.hserver.core.server.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import cn.hserver.core.server.context.ConfigMap;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

import static cn.hserver.core.server.context.ConstConfig.profiles;

/**
 * @author hxm
 */
public class PropUtil {
    private static final Logger log = LoggerFactory.getLogger(PropUtil.class);

    private static PropUtil propUtil;

    private static final ConfigMap data = new ConfigMap();

    private PropUtil() {
    }

    public static PropUtil getInstance() {
        if (propUtil != null) {
            return propUtil;
        } else {
            propUtil = new PropUtil();
            initProp();
            initYaml();
            return propUtil;
        }
    }

    private static String getPropFiles(String name) {
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
            p.forEach((k, v) -> data.put(toCamelCase(k.toString()), v.toString()));
            is.close();
        } catch (Exception e) {
            log.error(e.getMessage(),e);
        }
        //优先级查代码的，再查配置的
        if (profiles == null) {
            profiles = data.get("env");
        }
        if (profiles != null) {
            try {
                InputStreamReader is2 = getFileStream(getPropFiles(profiles));
                p.clear();
                if (is2 == null) {
                    return;
                }
                p.load(is2);
                p.forEach((k, v) -> data.put(toCamelCase(k.toString()), v.toString()));
                is2.close();
                p.clear();
            } catch (Exception e) {
                log.error(e.getMessage(),e);
            }
        }
    }


    private static String getYamlFiles(String name) {
        return name == null ? null : "/app-" + name + ".yml";
    }


    private static void initYaml() {
        Yaml yaml = new Yaml();
        try {
            String name = "/app.yml";
            InputStreamReader is = getFileStream(name);
            if (is == null) {
                return;
            }
            Map<String, Object> configData  = yaml.load(is);
            Map<String,Object> configMap = new HashMap<>();
            convertToProperties(configData,configMap,"");
            configMap.forEach((k, v) -> data.put(toCamelCase(k), v.toString()));
            configMap.clear();
            configData.clear();
            is.close();
        } catch (Exception e) {
            log.error(e.getMessage(),e);
        }
        //优先级查代码的，再查配置的
        if (profiles == null) {
            profiles = data.get("env");
        }
        if (profiles != null) {
            try {
                InputStreamReader is2 = getFileStream(getYamlFiles(profiles));
                if (is2 == null) {
                    return;
                }
                Map<String, Object> configData  = yaml.load(is2);
                Map<String,Object> configMap = new HashMap<>();
                convertToProperties(configData,configMap,"");
                configMap.forEach((k, v) -> data.put(toCamelCase(k), v.toString()));
                configMap.clear();
                configData.clear();
                is2.close();
            } catch (Exception e) {
                log.error(e.getMessage(),e);
            }
        }
    }


    private static void convertToProperties(Map<String, Object> data, Map<String,Object> properties, String prefix) {
        for (Map.Entry<String, Object> entry : data.entrySet()) {
            String key = prefix + entry.getKey().trim();
            Object value = entry.getValue();

            if (value instanceof Map) {
                @SuppressWarnings("unchecked")
                Map<String, Object> subMap = (Map<String, Object>) value;
                convertToProperties(subMap, properties, key + ".");
            } else {
                properties.put(key, value.toString());
            }
        }
    }

    private static String toCamelCase(String key) {
        StringBuilder result = new StringBuilder();
        boolean toUpperCase = false;

        for (char ch : key.toCharArray()) {
            if (ch == '-') {
                toUpperCase = true;
            } else {
                if (toUpperCase) {
                    result.append(Character.toUpperCase(ch));
                    toUpperCase = false;
                } else {
                    result.append(ch);
                }
            }
        }

        return result.toString();
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
        if (s != null && !s.trim().isEmpty()) {
            try {
                return (int) Calculator.calculate(s);
            } catch (Exception e) {
                return null;
            }
        }
        return null;
    }

    public Boolean getBoolean(String key) {
        String s = get(key);
        if (s != null && !s.trim().isEmpty()) {
            try {
                return Boolean.valueOf(s);
            } catch (Exception e) {
                return false;
            }
        }
        return false;
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
            return new InputStreamReader(Files.newInputStream(Paths.get(rootPath + path)), StandardCharsets.UTF_8);
        } catch (Exception e) {
            try {
                return new InputStreamReader(Objects.requireNonNull(PropUtil.class.getResourceAsStream(path)), StandardCharsets.UTF_8);
            } catch (Exception e1) {
                return null;
            }
        }
    }

}
