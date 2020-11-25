package top.hserver.core.api;

import lombok.Data;

import java.util.List;

/**
 * @author hxm
 */
@Data
public class ApiResult {
  /**
   * 控制器名字
   */
  private String name;
  /**
   * 控制器下的所有方法
   */
  private List<ApiData> apiData;
}
