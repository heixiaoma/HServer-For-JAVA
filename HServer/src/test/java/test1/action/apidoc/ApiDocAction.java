package test1.action.apidoc;

import test1.bean.User;
import top.hserver.core.ioc.annotation.Controller;
import top.hserver.core.ioc.annotation.GET;
import top.hserver.core.ioc.annotation.PUT;
import top.hserver.core.ioc.annotation.apidoc.ApiImplicitParam;
import top.hserver.core.ioc.annotation.apidoc.ApiImplicitParams;
import top.hserver.core.ioc.annotation.apidoc.DataType;
import top.hserver.core.server.util.JsonResult;


@Controller(value = "/v1/Api", name = "Api接口1")
public class ApiDocAction {

  /**
   * 访问 get:/v1/Api/get
   *
   * @return
   */
  @GET("/get")
  @ApiImplicitParams(
    value = {
      @ApiImplicitParam(name = "name", value = "名字", required = true, dataType = DataType.String),
      @ApiImplicitParam(name = "sex", value = "性别", required = true, dataType = DataType.Integer),
      @ApiImplicitParam(name = "age", value = "年龄", required = true, dataType = DataType.Integer),
    },
    note = "这是一个Api的Get方法",
    name = "阿皮获取GET"
  )
  public JsonResult get(User user) {
    return JsonResult.ok().put("data", user);
  }

  /**
   * 访问 put:/v1/Api/put
   *
   * @return
   */
  @PUT("/put")
  @ApiImplicitParams(
    value = {
      @ApiImplicitParam(name = "name", value = "名字", required = true, dataType = DataType.String),
      @ApiImplicitParam(name = "sex", value = "性别", required = true, dataType = DataType.Integer),
      @ApiImplicitParam(name = "age", value = "年龄", required = true, dataType = DataType.Integer),
    },
    note = "这是一个Api的Put方法",
    name = "阿皮获取PUT"
  )
  public JsonResult put(User user) {
    return JsonResult.ok().put("data", user);
  }

}
