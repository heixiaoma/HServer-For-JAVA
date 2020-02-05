package top.hserver.core.server.router;

import io.netty.handler.codec.http.HttpMethod;
import lombok.extern.slf4j.Slf4j;
import top.hserver.core.server.context.PatternUri;
import top.hserver.core.server.context.WebContext;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static io.netty.handler.codec.http.HttpMethod.GET;

@Slf4j
public class RouterManager {

    /**
     * 路由线程池，关系映射
     */
    private final static Map<String, RouterInfo> routerGets = new ConcurrentHashMap<>();
    private final static Map<String, RouterInfo> routerPosts = new ConcurrentHashMap<>();

    private final static Map<String, RouterPermission> routerPermissionGets = new ConcurrentHashMap<>();
    private final static Map<String, RouterPermission> routerPermissionPosts = new ConcurrentHashMap<>();

    /**
     * 记录url是否是需要url正则匹配的
     */

    private final static Map<String, PatternUri> ISPAURI_GET = new ConcurrentHashMap<>();
    private final static Map<String, PatternUri> ISPAURI_POST = new ConcurrentHashMap<>();

    public static void addRouter(RouterInfo routerInfo) {
        if (routerInfo != null) {
            String url = routerInfo.getUrl();
            if (GET == routerInfo.reqMethodName) {

                /**
                 * 检查是否是需要匹配的那种URL
                 */
                String pattern = isPattern(url);
                if (pattern != null) {
                    String s = url.replaceAll("\\{" + pattern + "\\}", "(.*)");
                    ISPAURI_GET.put(s, new PatternUri(pattern, url,s));
                }

                if (routerGets.containsKey(url)) {
                    log.warn("url< {} >映射已经存在，可能会影响程序使用", url);
                }
                routerGets.put(url, routerInfo);
            } else {
                /**
                 * 检查是否是需要匹配的那种URL
                 */
                String pattern = isPattern(url);
                if (pattern != null) {
                    String s = url.replaceAll("\\{" + pattern + "\\}", "(.*)");
                    ISPAURI_POST.put(s, new PatternUri(pattern, url,s));
                }

                if (routerPosts.containsKey(url)) {
                    log.warn("url< {} >映射已经存在，可能会影响程序使用", url);
                }
                routerPosts.put(url, routerInfo);
            }
        }
    }


    private static String isPattern(String url) {
        String regex = "(\\{.*\\})";
        Matcher matcher = Pattern.compile(regex).matcher(url);
        if (matcher.find()) {
            String group = matcher.group(1);
            if (group!=null){
                return group.substring(1,group.length()-1);
            }
        }
        return null;
    }

    private static PatternUri isPattern(String url, HttpMethod method) {

        if (method == GET) {
            Iterator<String> iterator = ISPAURI_GET.keySet().iterator();
            while (iterator.hasNext()) {
                String next = iterator.next();
                if (Pattern.compile(next).matcher(url).find()) {
                    return ISPAURI_GET.get(next);
                }
            }
        } else {
            Iterator<String> iterator = ISPAURI_POST.keySet().iterator();
            while (iterator.hasNext()) {
                String next = iterator.next();
                if (Pattern.compile(next).matcher(url).find()) {
                    return ISPAURI_POST.get(next);
                }
            }
        }
        return null;
    }

    public static void addPermission(RouterPermission routerPermission) {
        if (routerPermission != null) {
            String url = routerPermission.getUrl();
            if (GET == routerPermission.getReqMethodName()) {
                if (routerPermissionGets.containsKey(url)) {
                    log.warn("url< {} >权限映射已经存在，可能会影响程序使用", url);
                }
                routerPermissionGets.put(url, routerPermission);
            } else {
                if (routerPermissionPosts.containsKey(url)) {
                    log.warn("url< {} >权限映射已经存在，可能会影响程序使用", url);
                }
                routerPermissionPosts.put(url, routerPermission);
            }
        }
    }


    public static RouterInfo getRouterInfo(String url, HttpMethod requestType, WebContext webContext) {
        Map<String, String> requestParams = webContext.getRequest().getRequestParams();
        if (GET == requestType) {
            RouterInfo routerInfo = routerGets.get(url);
            //默认检查一次正常URl
            if (routerInfo != null) {
                return routerInfo;
            } else {
                //二次检查匹配规则的URL;
                PatternUri pattern = isPattern(url, requestType);
                if (pattern != null) {
                    Matcher matcher = Pattern.compile(pattern.getPatternUrl()).matcher(url);
                    if (matcher.find()){
                        try {
                            requestParams.put(pattern.getKey(),URLDecoder.decode(matcher.group(1), "UTF-8"));
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                    }
                    return routerGets.get(pattern.getOrgUrl());
                }
            }
        } else {
            RouterInfo routerInfo = routerPosts.get(url);
            //默认检查一次正常URl
            if (routerInfo != null) {
                return routerInfo;
            } else {
                //二次检查匹配规则的URL;
                PatternUri pattern = isPattern(url, requestType);
                if (pattern != null) {
                    Matcher matcher = Pattern.compile(pattern.getPatternUrl()).matcher(url);
                    if (matcher.find()){
                        try {
                            requestParams.put(pattern.getKey(),URLDecoder.decode(matcher.group(1), "UTF-8"));
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                    }
                    return routerPosts.get(pattern.getOrgUrl());
                }
            }
        }
        return null;
    }


    public static RouterPermission getRouterPermission(String url, HttpMethod requestType) {
        if (GET == requestType) {
            RouterPermission routerPermission = routerPermissionGets.get(url);
            if(routerPermission!=null){
                return routerPermission;
            }else {
                PatternUri pattern = isPattern(url, requestType);
                if (pattern!=null){
                    return routerPermissionGets.get(pattern.getOrgUrl());
                }
            }
        } else {
            RouterPermission routerPermission = routerPermissionPosts.get(url);
            if(routerPermission!=null){
                return routerPermission;
            }else {
                PatternUri pattern = isPattern(url, requestType);
                if (pattern!=null){
                    return routerPermissionPosts.get(pattern.getOrgUrl());
                }
            }
        }
        return null;
    }

    public static List<RouterPermission> getRouterPermissions() {
        List<RouterPermission> permissions = new ArrayList<>();
        routerPermissionGets.forEach((a, b) -> permissions.add(b));
        routerPermissionPosts.forEach((a, b) -> permissions.add(b));
        return permissions;
    }
}
