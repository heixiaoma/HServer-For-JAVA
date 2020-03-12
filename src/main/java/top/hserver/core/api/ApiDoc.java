package top.hserver.core.api;

import top.hserver.core.ioc.annotation.*;
import top.hserver.core.ioc.annotation.apidoc.ApiImplicitParam;
import top.hserver.core.ioc.annotation.apidoc.ApiImplicitParams;
import top.hserver.core.ioc.ref.ClasspathPackageScanner;
import top.hserver.core.ioc.ref.PackageScanner;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Api 数据操作对象
 */
public class ApiDoc {

  private PackageScanner packageScanner;

  //第一步开始扫描应该有的包对象
  public ApiDoc(Class<?> baseClass) {
    this.packageScanner = new ClasspathPackageScanner(baseClass.getPackage().getName());
  }

  public ApiDoc(String packageName) {
    this.packageScanner = new ClasspathPackageScanner(packageName);
  }

  public List<ApiResult> getApiData() throws Exception {
    List<ApiResult> apiResults = new ArrayList<>();
    List<Class<?>> annotationList = packageScanner.getAnnotationList(Controller.class);
    //所有的控制器
    for (Class<?> aClass : annotationList) {
      Controller controller = aClass.getAnnotation(Controller.class);
      ApiResult apiResult = new ApiResult();
      boolean flag = false;
      //所有的方法
      Method[] methods = aClass.getMethods();
      List<ApiData> apiDatas = new ArrayList<>();
      for (Method method : methods) {
        ApiImplicitParams annotation = method.getAnnotation(ApiImplicitParams.class);
        if (annotation != null) {
          flag = true;
          //字段数据
          ApiImplicitParam[] value = annotation.value();
          List<ApiData.ReqData> reqDataList = new ArrayList<>();
          for (ApiImplicitParam apiImplicitParam : value) {
            ApiData.ReqData reqData = new ApiData.ReqData();
            reqData.setDataType(apiImplicitParam.dataType());
            reqData.setName(apiImplicitParam.name());
            reqData.setRequired(apiImplicitParam.required());
            reqData.setValue(apiImplicitParam.value());
            reqDataList.add(reqData);
          }
          //方法描述
          ApiData apiData = new ApiData();
          apiData.setName(annotation.name());
          apiData.setNote(annotation.note());
          apiData.setReqDataList(reqDataList);
          ApiData reqMethods = getReqMethods(method, controller.value().trim());
          //设置URL
          apiData.setUrl(reqMethods.getUrl());
          apiData.setRequestMethod(reqMethods.getRequestMethod());
          apiDatas.add(apiData);
        }
      }
      if (flag) {
        apiResult.setApiData(apiDatas);
        if (controller.name().trim().length() == 0) {
          apiResult.setName(aClass.getName());
        } else {
          apiResult.setName(controller.name());
        }
        apiResults.add(apiResult);
      }
    }
    return apiResults;
  }


  private ApiData getReqMethods(Method method, String controllerPath) {
    Class[] classes = new Class[]{GET.class,HEAD.class, POST.class, PUT.class, PATCH.class, DELETE.class, OPTIONS.class, CONNECT.class, TRACE.class, RequestMapping.class};
    List<String> reqNames = new ArrayList<>();
    ApiData apiData = new ApiData();
    for (Class aClass1 : classes) {
      Annotation annotation = method.getAnnotation(aClass1);
      if (annotation != null) {
        try {
          Method value = annotation.getClass().getMethod("value");
          Object invoke = value.invoke(annotation);
          apiData.setUrl(controllerPath + invoke.toString().trim());
        } catch (Exception ignored) {
        }
        if (aClass1 == RequestMapping.class) {
          RequestMapping annotation1 = (RequestMapping) annotation;
          if (annotation1.method() == null || annotation1.method().length == 0) {
            String[] requestMethodAll = RequestMethod.getRequestMethodAll();
            for (String s : requestMethodAll) {
              reqNames.add(s);
            }
          } else {
            for (RequestMethod requestMethod : annotation1.method()) {
              reqNames.add(requestMethod.toString());
            }
          }
        } else {
          reqNames.add(aClass1.getSimpleName());
        }
      }
    }
    apiData.setRequestMethod(reqNames);
    return apiData;
  }
}