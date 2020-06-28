package top.hserver.core.server.util;

import java.io.*;
import java.util.Properties;

/**
 * @author hxm
 */
public class PropUtil {

  private static PropUtil propUtil;

  private String name = "/application.properties";

  public PropUtil() {
  }

  public PropUtil(String fileName) {
    this.name = fileName;
  }

  public static PropUtil getInstance() {
    if (propUtil != null) {
      return propUtil;
    } else {
      propUtil = new PropUtil();
      return propUtil;
    }
  }


  public String get(String key) {
    String value = "";
    try (InputStream is = PropUtil.class.getResourceAsStream(name)) {
      Properties p = new Properties();
      p.load(is);
      value = p.getProperty(key);
    } catch (Exception ignored) {

    }
    return value;
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