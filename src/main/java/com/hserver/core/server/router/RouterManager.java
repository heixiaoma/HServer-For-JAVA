package com.hserver.core.server.router;

import com.hserver.core.ioc.IocUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class RouterManager {

    /**
     * 路由线程池，关系映射
     */
    private final static Map<String, RouterInfo> routerGets = new ConcurrentHashMap<>();
    private final static Map<String, RouterInfo> routerPosts = new ConcurrentHashMap<>();


    public static void addRouter(RouterInfo routerInfo) {
        if (routerInfo != null) {
            String url = routerInfo.getUrl();
            if (RequestType.GET.equals(routerInfo.reqMethodName)) {
                if (routerGets.containsKey(url)) {
                    log.warn("url<" + url + ">映射已经存在，可能会影响程序使用" + routerInfo.getaClass().getClass().getName());
                }
                routerGets.put(url, routerInfo);
            } else {
                if (routerPosts.containsKey(url)) {
                    log.warn("url<" + url + ">映射已经存在，可能会影响程序使用" + routerInfo.getaClass().getClass().getName());
                }
                routerPosts.put(url, routerInfo);
            }
        }
    }

    public static Object getRouterObj(String url, RequestType requestType) {
        if (RequestType.GET.equals(requestType)) {
            RouterInfo routerInfo = routerGets.get(url);
            return IocUtil.getBean(routerInfo.getaClass());
        } else {
            RouterInfo routerInfo = routerPosts.get(url);
            return IocUtil.getBean(routerInfo.getaClass());
        }
    }

    public static RouterInfo getRouterInfo(String url, RequestType requestType) {
        if (RequestType.GET.equals(requestType)) {
            return routerGets.get(url);
        } else {
            return routerPosts.get(url);
        }
    }

}
