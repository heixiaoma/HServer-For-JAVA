## **ApiDoc生成功能**

_关于这个api文档生成目前只是一个简洁版，在未来日子里相信会变得更好_

第一步

```java
@Controller(value = "/v1/Api2", name = "Api接口2")
class ApiController{}
//value值会自动补全类中方法的URL，name值，在文档中有名字定义的作用，如果这个名字不定义，那么会采用控制器的全路径。
```

第二步

```java
在需要生成注解的方法上，添加这个注解，这个注解类似swagger的注解。

  @GET("/get")
  @ApiImplicitParams(
    value = {
      @ApiImplicitParam(name = "name", value = "名字", required = true, dataType = DataType.String),
      @ApiImplicitParam(name = "sex", value = "性别", required = true, dataType = DataType.Integer),
      @ApiImplicitParam(name = "age", value = "年龄", required = true, dataType = DataType.Integer),
    },
    note = "这是一个Api的Get方法",
    name = "api获取GET"
  )
  public JsonResult get(User user) {
    return JsonResult.ok().put("data", user);
  }
```

第三步

HServer提供了一个叫ApiDoc的类，对他进行实例化，就可以获取到生成文档的对象，你可以进行自己的文档生成定制，

或者使用HServer提供 的简洁版本的文档模板 hserver_doc.ftl 需要将依赖里的这个文件copy到你的模板里面.

下面就是例子，ApiDoc的构造器可以传入class类型，或者传入String类型，主要目的是进行扫包，可以直接传入包名，或者传入class，然后获取包名


```java
  //官方模板输出
  @GET("/api")
  public void getApiData(HttpResponse httpResponse) {
    //ApiDoc apiDoc = new ApiDoc("top.test");
    ApiDoc apiDoc = new ApiDoc(TestWebApp.class);
    try {
      List<ApiResult> apiData = apiDoc.getApiData();
      HashMap<String,Object> stringObjectHashMap=new HashMap<>();
      stringObjectHashMap.put("data",apiData);
      httpResponse.sendTemplate("hserver_doc.ftl",stringObjectHashMap);
    }catch (Exception e){
      httpResponse.sendJson(JsonResult.error());
    }
  }
  
  //输出json,或者自己自定名字  
  @GET("/apiJson")
  public JsonResult getApiDataa() {
    ApiDoc apiDoc = new ApiDoc("top.test");
    try {
      List<ApiResult> apiData = apiDoc.getApiData();
      return JsonResult.ok().put("data",apiData);
    }catch (Exception e){
      return JsonResult.error();
    }
  }
```
![AB测试](https://gitee.com/HServer/HServer/raw/master/doc/apidoc.png)
