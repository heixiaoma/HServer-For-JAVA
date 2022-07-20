package cn.hserver.plugin.web.api;

import cn.hserver.core.ioc.annotation.RequestMethod;
import cn.hserver.core.ioc.ref.ClasspathPackageScanner;
import cn.hserver.core.ioc.ref.PackageScanner;
import cn.hserver.plugin.web.annotation.*;
import cn.hserver.plugin.web.annotation.apidoc.ApiImplicitParam;
import cn.hserver.plugin.web.annotation.apidoc.ApiImplicitParams;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.text.Collator;
import java.util.*;

/**
 * Api 数据操作对象
 *
 * @author hxm
 */
public class ApiDoc {

    private PackageScanner packageScanner;

    /**
     * 第一步开始扫描应该有的包对象
     *
     * @param baseClass
     */
    public ApiDoc(Class<?> baseClass) {
        this.packageScanner = new ClasspathPackageScanner(new HashSet<String>() {
            {
                add(baseClass.getPackage().getName());
            }
        });
    }

    public ApiDoc(String packageName) {
        this.packageScanner = new ClasspathPackageScanner(new HashSet<String>() {
            {
                add(packageName);
            }
        });
    }

    public List<ApiResult> getApiData() throws Exception {
        List<ApiResult> apiResults = new ArrayList<>();
        Set<Class<?>> annotationList = packageScanner.getAnnotationList(Controller.class);
        //所有的控制器
        for (Class<?> aClass : annotationList) {
            Controller controller = aClass.getAnnotation(Controller.class);
            ApiResult apiResult = new ApiResult();
            boolean flag = false;
            //所有的方法
            Method[] methods = aClass.getMethods();
            List<ApiData> amiDates = new ArrayList<>();
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
                    amiDates.add(apiData);
                }
            }
            if (flag) {
                apiResult.setApiData(amiDates);
                if (controller.name().trim().length() == 0) {
                    apiResult.setName(aClass.getName());
                } else {
                    apiResult.setName(controller.name());
                }
                apiResults.add(apiResult);
            }
        }
        return sort(apiResults);
    }

    private List<ApiResult> sort(List<ApiResult> apiData) {
        for (ApiResult apiDatum : apiData) {
            List<ApiData> apiData1 = apiDatum.getApiData();
            Collections.sort(apiData1, (o1, o2) -> {
                Collator collator = Collator.getInstance(Locale.CHINA);
                return collator.compare(o1.getName(), o2.getName());
            });
        }
        Collections.sort(apiData, (o1, o2) -> {
            Collator collator = Collator.getInstance(Locale.CHINA);
            return collator.compare(o1.getName(), o2.getName());
        });
        return apiData;
    }

    private ApiData getReqMethods(Method method, String controllerPath) {
        Class[] classes = new Class[]{GET.class, HEAD.class, POST.class, PUT.class, PATCH.class, DELETE.class, OPTIONS.class, CONNECT.class, TRACE.class, RequestMapping.class};
        List<String> reqNames = new ArrayList<>();
        ApiData apiData = new ApiData();
        for (Class aClass1 : classes) {
            Annotation annotation = method.getAnnotation(aClass1);
            if (annotation != null) {
                try {
                    Method value = annotation.getClass().getMethod("value");
                    value.setAccessible(true);
                    Object invoke = value.invoke(annotation);
                    apiData.setUrl(controllerPath + invoke.toString().trim());
                } catch (Exception ignored) {
                }
                if (aClass1 == RequestMapping.class) {
                    RequestMapping annotation1 = (RequestMapping) annotation;
                    if (annotation1.method().length == 0) {
                        String[] requestMethodAll = RequestMethod.getRequestMethodAll();
                        Collections.addAll(reqNames, requestMethodAll);
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
