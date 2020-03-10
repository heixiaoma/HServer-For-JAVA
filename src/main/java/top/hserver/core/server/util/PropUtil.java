package top.hserver.core.server.util;

import java.io.*;
import java.util.Properties;

public class PropUtil {

  private String name = "/application.properties";

  public PropUtil() {

  }

  public PropUtil(String fileName) {
    this.name = fileName;
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

  public Properties getProperties() {
    Properties p = new Properties();
    try (InputStream is = PropUtil.class.getClassLoader().getResourceAsStream(name)) {
      p.load(is);
    } catch (IOException ignored) {
    }
    return p;
  }

  /**
   * 往properties文件中写入key-value键值对
   *
   * @param key
   * @param value
   */
  public void set(String key, String value) {
    InputStream is = null;
    OutputStream os = null;
    Properties p = new Properties();
    try {
      is = new FileInputStream(PropUtil.class.getClassLoader().getResource(name).getFile());
      p.load(is);
      os = new FileOutputStream(PropUtil.class.getClassLoader().getResource(name).getFile());

      p.setProperty(key, value);
      p.store(os, key);
      os.flush();
      os.close();
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      try {
        if (null != is) {
          is.close();
        }
        if (null != os) {
          os.close();
        }
      } catch (IOException e) {
        e.printStackTrace();
      }
    }

  }
}