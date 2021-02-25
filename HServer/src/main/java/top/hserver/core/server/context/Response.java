package top.hserver.core.server.context;

import io.netty.handler.codec.http.HttpResponseStatus;
import top.hserver.core.interfaces.HttpResponse;
import top.hserver.core.server.util.FreemarkerUtil;

import java.io.File;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author hxm
 */
public class Response implements HttpResponse {

  private Map<String, String> headers = new ConcurrentHashMap<>();

  private File file;

  private InputStream inputStream;

  private boolean isDownload = false;

  private String fileName;

  private String result = null;

  private HttpResponseStatus httpResponseStatus;

  private boolean isProxy=false;


  @Override
  public boolean hasData() {
    //重定向，json html 等
    if (result!=null){
      return true;
    }
    //下载文件
    if (isDownload){
      return true;
    }
    return false;
  }

  /**
   * 设置响应头
   *
   * @param key
   * @param value
   */
  @Override
  public void setHeader(String key, String value) {
    this.headers.put(key, value);
  }

  /**
   * 下载文件
   *
   * @param file
   */
  @Override
  public void setDownloadFile(File file) {
    this.file = file;
    this.isDownload = true;
    this.fileName = file.getName();
  }

  /**
   * 下载文件啦
   *
   * @param inputStream
   */
  @Override
  public void setDownloadFile(InputStream inputStream, String fileName) {
    this.inputStream = inputStream;
    this.isDownload = true;
    this.fileName = fileName;
  }


  @Override
  public void sendJsonString(String jsonStr) {
    this.result = jsonStr;
    headers.put("content-type", "application/json;charset=UTF-8");
  }

  @Override
  public void sendJson(Object object) {
    try {
      this.result = ConstConfig.OBJECT_MAPPER.writeValueAsString(object);
      headers.put("content-type", "application/json;charset=UTF-8");
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Override
  public void sendHtml(String html) {
    this.result = html;
    headers.put("content-type", "text/html;charset=UTF-8");
  }

  @Override
  public void sendText(String text) {
    this.result = text;
    headers.put("content-type", "text/plain;charset=UTF-8");
  }

  /**
   * 设置一个空字符
   */
  public void sendNull() {
    this.result = "";
  }

  @Override
  public void sendStatusCode(HttpResponseStatus httpResponseStatus) {
    this.httpResponseStatus = httpResponseStatus;
  }

  @Override
  public void isProxy(boolean p) {
    this.isProxy=p;
  }

  @Override
  public void sendTemplate(String htmlPath, Map<String, Object> obj) {
    try {
      this.result = FreemarkerUtil.getTemplate(htmlPath, obj);
    } catch (Exception e) {
      e.printStackTrace();
    }
    headers.put("content-type", "text/html;charset=UTF-8");
  }

  @Override
  public void sendTemplate(String htmlPath) {
    this.sendTemplate(htmlPath,new HashMap<>(0));
  }

  /**
   * 添加Cookie
   *
   * @param cookie
   */
  @Override
  public void addCookie(Cookie cookie) {
    Iterator<String> iterator = cookie.keySet().iterator();
    StringBuilder cookieStr = new StringBuilder();
    while (iterator.hasNext()) {
      String k = iterator.next();
      String v = cookie.get(k);
      try {
        cookieStr.append(java.net.URLEncoder.encode(k, "UTF-8") + "=" + java.net.URLEncoder.encode(v, "UTF-8") + ";");
      } catch (UnsupportedEncodingException e) {
        e.printStackTrace();
      }
    }
    if (cookie.getMaxAge() != null) {
      cookieStr.append("Max-Age=");
      cookieStr.append(cookie.getMaxAge());
      cookieStr.append(";");
    }
    if (cookie.getPath() != null) {
      cookieStr.append("path=");
      cookieStr.append(cookie.getPath());
      cookieStr.append(";");
    }
    headers.put("Set-Cookie", cookieStr.toString());
  }

  @Override
  public void redirect(String url) {
    this.result="";
    headers.put("location", url);
  }


  //---------------系统用的Get操作

  public Map<String, String> getHeaders() {
    return headers;
  }

  public File getFile() {
    return file;
  }

  public InputStream getInputStream() {
    return inputStream;
  }

  public boolean isDownload() {
    return isDownload;
  }

  public String getFileName() {
    return fileName;
  }

  public String getResult() {
    return result;
  }

  public void setResult(String result) {
    this.result = result;
  }

  public HttpResponseStatus getHttpResponseStatus() {
    return httpResponseStatus;
  }

  public boolean isProxy() {
    return isProxy;
  }

}
