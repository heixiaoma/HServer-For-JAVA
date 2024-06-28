package cn.hserver.plugin.web.router;

import cn.hserver.plugin.web.annotation.RequestMethod;
import cn.hserver.plugin.web.context.HServerContext;
import cn.hserver.plugin.web.context.PatternUri;
import cn.hserver.plugin.web.context.Request;
import cn.hserver.plugin.web.exception.MethodNotSupportException;
import io.netty.handler.codec.http.HttpMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import cn.hserver.core.server.util.ExceptionUtil;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author hxm
 */
public class RouterManager {

    private static final Logger log = LoggerFactory.getLogger(RouterManager.class);

    /**
     * 池子 <uri,<method,method_info>>
     */
    private final static Map<String, Map<String, RouterInfo>> router2 = new ConcurrentHashMap<>();

    /**
     * 记录url是否是需要url正则匹配的
     * 池子 <uri,<method,method_info>>
     */
    private final static Map<String, Map<String, PatternUri>> ISPAURI = new ConcurrentHashMap<>();

    /**
     * 存储路由权限
     */
    private final static Map<HttpMethod, Map<String, RouterPermission>> routerPermission = new ConcurrentHashMap<>();


    private static Map<String, RouterPermission> routerPermission(HttpMethod method) {
        return routerPermission.computeIfAbsent(method, k -> new ConcurrentHashMap<>());
    }


    public static void addRouter(RouterInfo routerInfo) {
        if (routerInfo != null) {
            String url = routerInfo.getUrl();
            //对URL检查是否是正则的 如果是正则的就进行替换为正则的方便后期校验
            List<String> pattern = isPattern(url);
            if (!pattern.isEmpty()) {
                String s = url;
                for (int i = 0; i < pattern.size(); i++) {
                    if (i == pattern.size() - 1) {
                        s = s.replaceAll("\\{" + pattern.get(i) + "}", "(.+)");
                    } else {
                        s = s.replaceAll("\\{" + pattern.get(i) + "}", "(.+?)");
                    }
                }
                s = "^" + s;
                Map<String, PatternUri> ispauri = ISPAURI.computeIfAbsent(s, k -> new ConcurrentHashMap<>());
                ispauri.put(routerInfo.getReqMethodName().name(), new PatternUri(pattern, url, s, routerInfo.getReqMethodName().name()));
            }

            Map<String, RouterInfo> httpMethodRouterInfoMap = router2.computeIfAbsent(url, k -> new ConcurrentHashMap<>());
            if (httpMethodRouterInfoMap.containsKey(routerInfo.getReqMethodName().name())) {
                log.warn("url< {} >映射路径已经存在，可能会影响程序使用，class:{},method:{}", url, routerInfo.getaClass().getName(), routerInfo.getMethod().getName());
            }
            httpMethodRouterInfoMap.put(routerInfo.getReqMethodName().name(), routerInfo);
        }
    }


    private static List<String> isPattern(String url) {
        String regex = "(\\{.*?})";
        Matcher matcher = Pattern.compile(regex).matcher(url);
        List<String> patterns = new ArrayList<>();
        while (matcher.find()) {
            String group = matcher.group(1);
            if (group != null) {
                patterns.add(group.substring(1, group.length() - 1));
            }
        }
        return patterns;
    }

    private static PatternUri isPattern(String url, HttpMethod method) throws MethodNotSupportException {
        for (String next : ISPAURI.keySet()) {
            if (Pattern.compile(next).matcher(url).find()) {
                Map<String, PatternUri> stringPatternUriMap = ISPAURI.get(next);
                if (stringPatternUriMap!=null){
                    PatternUri patternUri = stringPatternUriMap.get(method.name());
                    if (patternUri!=null){
                        return patternUri;
                    }
                    throw new MethodNotSupportException();
                }
            }
        }
        return null;
    }

    public static void addPermission(RouterPermission routerPermission) {
        if (routerPermission != null) {
            String url = routerPermission.getUrl();
            Map<String, RouterPermission> stringRouterPermissionMap = routerPermission(routerPermission.getReqMethodName());
            if (stringRouterPermissionMap.containsKey(url)) {
                log.warn("url< {} >权限映射已经存在，可能会影响程序使用", url);
            }
            stringRouterPermissionMap.put(url, routerPermission);
        }
    }


    public static RouterInfo getRouterInfo(String url, HttpMethod requestType, HServerContext hServerContext) throws MethodNotSupportException {
        Request request = hServerContext.getRequest();
        Map<String, RouterInfo> router = router2.get(url);
        if (router == null) {
            //二次检查匹配规则的URL;
            PatternUri pattern = isPattern(url, requestType);
            if (pattern != null) {
                //用初始的规则匹配现在的key 值，
                Matcher matcher = Pattern.compile(pattern.getPatternUrl()).matcher(url);
                if (matcher.find()) {
                    //对控制器的参数进行拼装。这个里的这个拼装类似指针调用，这里put webContent也put了。
                    for (int i = 0; i < pattern.getKeys().size(); i++) {
                        try {
                            request.addReqParams(pattern.getKeys().get(i), URLDecoder.decode(matcher.group(i + 1), "UTF-8"));
                            request.addReqUrlParams(pattern.getKeys().get(i), URLDecoder.decode(matcher.group(i + 1), "UTF-8"));
                        } catch (UnsupportedEncodingException e) {
                            log.error(e.getMessage(),e);
                        }
                    }

                }
                Map<String, RouterInfo> stringRouterInfoMap = router2.get(pattern.getOrgUrl());
                if (stringRouterInfoMap == null) {
                    return null;
                }
                return stringRouterInfoMap.get(pattern.getRequestType());
            }
            return null;
        }


        RouterInfo routerInfo = router.get(requestType.name());
        //默认检查一次正常URl
        if (routerInfo != null) {
            return routerInfo;
        } else {
            throw new MethodNotSupportException();
        }
    }


    public static RouterPermission getRouterPermission(String url, HttpMethod requestType) {
        Map<String, RouterPermission> stringRouterPermissionMap = routerPermission(requestType);
        RouterPermission routerPermission = stringRouterPermissionMap.get(url);
        if (routerPermission != null) {
            return routerPermission;
        } else {
            try {
                PatternUri pattern = isPattern(url, requestType);
                if (pattern != null) {
                    return stringRouterPermissionMap.get(pattern.getOrgUrl());
                }
            }catch (MethodNotSupportException e){
                return null;
            }
        }
        return null;
    }

    public static List<RouterPermission> getRouterPermissions() {
        List<RouterPermission> permissions = new ArrayList<>();
        String[] requestMethodAll = RequestMethod.getRequestMethodAll();
        for (String s : requestMethodAll) {
            HttpMethod httpMethod = HttpMethod.valueOf(s);
            Map<String, RouterPermission> stringRouterPermissionMap = routerPermission(httpMethod);
            stringRouterPermissionMap.forEach((a, b) -> permissions.add(b));
        }
        return permissions;
    }
}
