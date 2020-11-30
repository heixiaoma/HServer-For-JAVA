package top.hserver.core.api;


import java.util.List;

/**
 * @author hxm
 */
public class ApiResult {
  /**
   * 控制器名字
   */
  private String name;
  /**
   * 控制器下的所有方法
   */
  private List<ApiData> apiData;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public List<ApiData> getApiData() {
    return apiData;
  }

  public void setApiData(List<ApiData> apiData) {
    this.apiData = apiData;
  }
}
