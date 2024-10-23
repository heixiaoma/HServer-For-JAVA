package cn.hserver.plugin.web.handlers.check;

import cn.hserver.core.ioc.IocUtil;
import cn.hserver.plugin.web.context.HServerContext;
import cn.hserver.plugin.web.exception.BusinessException;
import cn.hserver.plugin.web.exception.MethodNotSupportException;
import cn.hserver.plugin.web.exception.NotFoundException;
import cn.hserver.plugin.web.router.RouterInfo;
import cn.hserver.plugin.web.router.RouterManager;
import cn.hserver.plugin.web.util.ParameterUtil;
import io.netty.handler.codec.http.HttpResponseStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class FindController implements DispatcherHandler{

    private static final Logger log = LoggerFactory.getLogger(FindController.class);


    @Override
    public HServerContext dispatcher(HServerContext hServerContext) {
        /**
         * 如果静态文件就跳过当前的处理，否则就去执行控制器的方法
         */
        if (hServerContext.isStaticFile()) {
            return hServerContext;
        }

        /**
         * 检查下Webkit是否设置了值
         *  文本输出
         *  下载
         *  重定向
         *
         */
        if (hServerContext.getWebkit().httpResponse.hasData()) {
            return hServerContext;
        }
        RouterInfo routerInfo;
        try {
            routerInfo = RouterManager.getRouterInfo(hServerContext.getRequest().getUri(), hServerContext.getRequest().getRequestType(), hServerContext);
            if (routerInfo == null) {
                String error = "未找到对应的控制器，请求方式：" +
                        hServerContext.getRequest().getRequestType().toString() +
                        "，请求路径：" +
                        hServerContext.getRequest().getUri() +
                        "，来源IP：" +
                        hServerContext.getRequest().getIpAddress();
                throw new BusinessException(HttpResponseStatus.NOT_FOUND.code(), error, new NotFoundException("不能找到处理当前请求的资源"), hServerContext.getWebkit());
            }
        }catch (MethodNotSupportException e){
            String error = "控制器不允许的请求方法：" +
                    hServerContext.getRequest().getRequestType().toString() +
                    "，请求路径：" +
                    hServerContext.getRequest().getUri() +
                    "，来源IP：" +
                    hServerContext.getRequest().getIpAddress();
            throw new BusinessException(HttpResponseStatus.METHOD_NOT_ALLOWED.code(), error,e, hServerContext.getWebkit());
        }
        Method method = routerInfo.getMethod();
        Class<?> aClass = routerInfo.getaClass();
        Object bean = routerInfo.getControllerRef();
        if (bean==null){
             bean = IocUtil.getBean(routerInfo.getaClass());
             routerInfo.setControllerRef(bean);
        }
        //检查下方法参数
        Object res;
        //如果控制器有参数，那么就进行，模糊赋值，在检测是否有req 和resp
        try {
            Object[] methodArgs;
            try {
                methodArgs = ParameterUtil.getMethodArgs(aClass, method, hServerContext);
            } catch (Exception e) {
                throw new BusinessException(HttpResponseStatus.INTERNAL_SERVER_ERROR.code(), "控制器方法调用时传入的参数异常", e, hServerContext.getWebkit());
            }
            if (!method.isAccessible()) {
                method.setAccessible(true);
            }
            res = method.invoke(bean, methodArgs);
            //调用结果进行设置
            if (res == null) {
                if (hServerContext.getResponse().getResult() == null) {
                    hServerContext.getResponse().sendNull();
                }
            } else if (String.class.getName().equals(res.getClass().getName())) {
                hServerContext.getResponse().sendText(res.toString());
            } else {
                //非字符串类型的将对象转换Json字符串
                try {
                    hServerContext.getResponse().sendJson(res);
                } catch (Exception e) {
                    throw new BusinessException(HttpResponseStatus.INTERNAL_SERVER_ERROR.code(), "返回的数据非字符串被转换JSON异常", e, hServerContext.getWebkit());
                }
            }
        } catch (InvocationTargetException e) {
            throw new BusinessException(HttpResponseStatus.INTERNAL_SERVER_ERROR.code(), "调用方法失败", e.getTargetException(), hServerContext.getWebkit());
        } catch (IllegalAccessException | IllegalArgumentException e) {
            throw new BusinessException(HttpResponseStatus.INTERNAL_SERVER_ERROR.code(), "调用控制器时参数异常", e, hServerContext.getWebkit());
        }
        return hServerContext;
    }
}
