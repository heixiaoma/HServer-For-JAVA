package cn.hserver.core.config;

import org.yaml.snakeyaml.Yaml;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.function.Function;

/**
 * 配置文件读取工具类，支持 YAML 和 Properties 格式
 */
public class ConfigData {
    private static final ConfigMap configMap = new ConfigMap();
    private final static ConfigData reader = new ConfigData();

    public static ConfigData getInstance() {
        return reader;
    }


    private ConfigData() {
        //加载默认配置
        loadConfig("app");
        //加载环境配置
        Object env = configMap.get("env");
        if (env != null && !env.toString().isEmpty()) {
            ConstConfig.EVN = env.toString();
        }
        //配置的的env或者环境变量的env都可以，优先配置文件
        if (ConstConfig.EVN != null) {
            loadConfig("app-" + ConstConfig.EVN);
        }

    }



    /**
     * 根据文件路径加载配置
     * @param name 配置文件名
     * @throws IOException 文件读取异常
     */
    private void loadConfig(String name){
        loadYamlConfig(name+".yaml");
        loadYamlConfig(name+".yml");
        loadPropertiesConfig(name+".properties");
    }

    /**
     * 加载 YAML 配置文件
     * @param name 文件名
     * @throws IOException 文件读取异常
     */
    private void loadYamlConfig(String name){
        try (InputStreamReader inputStream = loadFileStream(name)) {
            Yaml yaml = new Yaml();
            Map<String, Object> yamlMap = yaml.load(inputStream);
            if (yamlMap != null) {
                flattenMap(yamlMap, "", configMap);
            }
        }catch (Exception e){}
    }

    /**
     * 加载 Properties 配置文件
     * @param name 文件名
     * @throws IOException 文件读取异常
     */
    private void loadPropertiesConfig(String name) {
        try (InputStreamReader inputStream = loadFileStream(name)) {
            Properties properties = new Properties();
            properties.load(inputStream);
            properties.forEach((key, value) -> configMap.put(key.toString(), value));
        }catch (Exception e){}
    }



    private InputStreamReader loadFileStream(String name){
        try {
            return new InputStreamReader(Files.newInputStream(Paths.get(ConstConfig.PATH + name)), StandardCharsets.UTF_8);
        } catch (Exception e) {
            try {
                return new InputStreamReader(Objects.requireNonNull(ConstConfig.class.getResourceAsStream("/"+name)), StandardCharsets.UTF_8);
            } catch (Exception e1) {
                return null;
            }
        }

    }


    /**
     * 将嵌套的 Map 展平为一级 Map，使用点号作为分隔符
     * @param source 源 Map
     * @param prefix 前缀
     * @param target 目标 Map
     */
    private void flattenMap(Map<String, Object> source, String prefix, Map<String, Object> target) {
        source.forEach((key, value) -> {
            String newKey = prefix.isEmpty() ? key : prefix + "." + key;
            if (value instanceof Map) {
                @SuppressWarnings("unchecked")
                Map<String, Object> nestedMap = (Map<String, Object>) value;
                flattenMap(nestedMap, newKey, target);
            } else {
                target.put(newKey, value);
            }
        });
    }

    /**
     * 获取配置的原始 Map
     * @return 配置 Map
     */
    public Map<String, Object> getConfigMap() {
        return new HashMap<>(configMap);
    }

    /**
     * 通过 key 获取配置值
     * @param key 键
     * @return 值
     */
    public Object get(String key) {
        return configMap.get(key);
    }

    /**
     * 通过 key 获取配置值，提供默认值
     * @param key 键
     * @param defaultValue 默认值
     * @return 值，如果不存在则返回默认值
     */
    public Object get(String key, Object defaultValue) {
        return configMap.getOrDefault(key, defaultValue);
    }

    /**
     * 通过 key 获取 String 类型配置值
     * @param key 键
     * @return String 值
     */
    public String getString(String key) {
        return getAndConvert(key, Object::toString);
    }

    /**
     * 通过 key 获取 String 类型配置值，提供默认值
     * @param key 键
     * @param defaultValue 默认值
     * @return String 值，如果不存在则返回默认值
     */
    public String getString(String key, String defaultValue) {
        return getAndConvert(key, Object::toString, defaultValue);
    }

    /**
     * 通过 key 获取 Integer 类型配置值
     * @param key 键
     * @return Integer 值
     */
    public Integer getInteger(String key) {
        return getAndConvert(key, Integer::valueOf);
    }

    /**
     * 通过 key 获取 Integer 类型配置值，提供默认值
     * @param key 键
     * @param defaultValue 默认值
     * @return Integer 值，如果不存在则返回默认值
     */
    public Integer getInteger(String key, Integer defaultValue) {
        return getAndConvert(key, Integer::valueOf, defaultValue);
    }

    /**
     * 通过 key 获取 Boolean 类型配置值
     * @param key 键
     * @return Boolean 值
     */
    public Boolean getBoolean(String key) {
        return getAndConvert(key, Boolean::valueOf);
    }

    /**
     * 通过 key 获取 Boolean 类型配置值，提供默认值
     * @param key 键
     * @param defaultValue 默认值
     * @return Boolean 值，如果不存在则返回默认值
     */
    public Boolean getBoolean(String key, Boolean defaultValue) {
        return getAndConvert(key, Boolean::valueOf, defaultValue);
    }

    /**
     * 通用转换方法
     * @param key 键
     * @param converter 转换器函数
     * @param <T> 目标类型
     * @return 转换后的值
     */
    private <T> T getAndConvert(String key, Function<String, T> converter) {
        Object value = configMap.get(key);
        if (value == null) {
            return null;
        }
        return converter.apply(value.toString());
    }

    /**
     * 通用转换方法，提供默认值
     * @param key 键
     * @param converter 转换器函数
     * @param defaultValue 默认值
     * @param <T> 目标类型
     * @return 转换后的值，如果不存在则返回默认值
     */
    private <T> T getAndConvert(String key, Function<String, T> converter, T defaultValue) {
        T value = getAndConvert(key, converter);
        return value != null ? value : defaultValue;
    }
}    