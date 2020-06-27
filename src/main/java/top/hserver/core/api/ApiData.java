package top.hserver.core.api;

import lombok.Data;
import top.hserver.core.ioc.annotation.apidoc.DataType;

import java.util.List;

/**
 * @author hxm
 */
@Data
public class ApiData {

  /**
   * 接口名字
   */
  private String name;
  /**
   * 请求地址
   */
  private String url;
  /**
   * 接口描述
   */
  private String note;
  /**
   * 请求类型
   */
  private List<String> requestMethod;
  /**
   * 请求的字段
   */
  private List<ReqData> reqDataList;

  @Data
  public static class ReqData {

    /**
     * 字段名字
     */
    private String name;
    /**
     * 字段描述
     */
    private String value;
    /**
     * 是否必填
     */
    private boolean required;
    /**
     * 数据类型
     */
    private DataType dataType;

  }
}
