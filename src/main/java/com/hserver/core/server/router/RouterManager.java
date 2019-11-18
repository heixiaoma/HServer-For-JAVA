package com.hserver.core.server.router;

import com.hserver.core.ioc.IocUtil;
import io.netty.handler.codec.http.HttpMethod;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static io.netty.handler.codec.http.HttpMethod.GET;

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
            if (GET == routerInfo.reqMethodName) {
                if (routerGets.containsKey(url)) {
                    log.warn("url<" + url + ">映射已经存在，可能会影响程序使用" );
                }
                routerGets.put(url, routerInfo);
            } else {
                if (routerPosts.containsKey(url)) {
                    log.warn("url<" + url + ">映射已经存在，可能会影响程序使用");
                }
                routerPosts.put(url, routerInfo);
            }
        }
    }

    public static RouterInfo getRouterInfo(String url, HttpMethod requestType) {
        if (GET == requestType) {
            return routerGets.get(url);
        } else {
            return routerPosts.get(url);
        }
    }

}
