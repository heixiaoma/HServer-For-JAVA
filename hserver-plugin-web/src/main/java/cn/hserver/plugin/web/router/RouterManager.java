package cn.hserver.plugin.web.router;

import io.netty.handler.codec.http.HttpMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import cn.hserver.core.ioc.annotation.RequestMethod;
import cn.hserver.core.server.context.HServerContext;
import cn.hserver.core.server.context.PatternUri;
import cn.hserver.core.server.context.Request;
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
     * 路由线程池，关系映射
     */
    private final static Map<HttpMethod, Map<String, RouterInfo>> router = new ConcurrentHashMap<>();

    private final static Map<HttpMethod, Map<String, RouterPermission>> routerPermission = new ConcurrentHashMap<>();

    /**
     * 清除
     */
    public static synchronized void clearRouterManager() {
        router.clear();
        routerPermission.clear();
    }


    /**
     * 记录url是否是需要url正则匹配的
     */
    private final static Map<HttpMethod, Map<String, PatternUri>> ISPAURI = new ConcurrentHashMap<>();

    private static Map<String, PatternUri> ISPAURI(HttpMethod method) {
        Map<String, PatternUri> stringPatternUriMap = ISPAURI.get(method);
        if (stringPatternUriMap == null) {
            stringPatternUriMap = new ConcurrentHashMap<>();
            ISPAURI.put(method, stringPatternUriMap);
        }
        return stringPatternUriMap;
    }

    private static Map<String, RouterPermission> routerPermission(HttpMethod method) {
        Map<String, RouterPermission> stringRouterPermissionMap = routerPermission.get(method);
        if (stringRouterPermissionMap == null) {
            stringRouterPermissionMap = new ConcurrentHashMap<>();
            routerPermission.put(method, stringRouterPermissionMap);
        }
        return stringRouterPermissionMap;
    }

    private static Map<String, RouterInfo> router(HttpMethod method) {
        Map<String, RouterInfo> stringRouterInfoMap = router.get(method);
        if (stringRouterInfoMap == null) {
            stringRouterInfoMap = new ConcurrentHashMap<>();
            router.put(method, stringRouterInfoMap);
        }
        return stringRouterInfoMap;
    }

    public static void addRouter(RouterInfo routerInfo) {
        if (routerInfo != null) {
            String url = routerInfo.getUrl();
            /**
             * 检查是否是需要匹配的那种URL
             */
            List<String> pattern = isPattern(url);
            if (pattern.size() > 0) {
                String s = url;
                for (int i = 0; i < pattern.size(); i++) {
                    if (i == pattern.size() - 1) {
                        s = s.replaceAll("\\{" + pattern.get(i) + "\\}", "(.+)");
                    } else {
                        s = s.replaceAll("\\{" + pattern.get(i) + "\\}", "(.+?)");
                    }
                }
                Map<String, PatternUri> ispauri = ISPAURI(routerInfo.getReqMethodName());
                if (ispauri != null) {
                    s = "^" + s;
                    ispauri.put(s, new PatternUri(pattern, url, s));
                }
            }
            Map<String, RouterInfo> router = router(routerInfo.getReqMethodName());
            if (router != null) {
                if (router.containsKey(url)) {
                    log.warn("url< {} >映射已经存在，可能会影响程序使用", url);
                }
                router.put(url, routerInfo);
            }
        }
    }


    private static List<String> isPattern(String url) {
        String regex = "(\\{.*?\\})";
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

    private static PatternUri isPattern(String url, HttpMethod method) {
        Map<String, PatternUri> ispauri = ISPAURI(method);
        if (ispauri == null) {
            return null;
        }
        Iterator<String> iterator = ispauri.keySet().iterator();
        while (iterator.hasNext()) {
            String next = iterator.next();
            if (Pattern.compile(next).matcher(url).find()) {
                return ispauri.get(next);
            }
        }
        return null;
    }

    public static void addPermission(RouterPermission routerPermission) {
        if (routerPermission != null) {
            String url = routerPermission.getUrl();
            Map<String, RouterPermission> stringRouterPermissionMap = routerPermission(routerPermission.getReqMethodName());
            if (stringRouterPermissionMap != null) {
                if (stringRouterPermissionMap.containsKey(url)) {
                    log.warn("url< {} >权限映射已经存在，可能会影响程序使用", url);
                }
                stringRouterPermissionMap.put(url, routerPermission);
            }
        }
    }


    public static RouterInfo getRouterInfo(String url, HttpMethod requestType, HServerContext hServerContext) {
        Request request = hServerContext.getRequest();
        Map<String, RouterInfo> router = router(requestType);
        if (router == null) {
            return null;
        }
        RouterInfo routerInfo = router.get(url);
        //默认检查一次正常URl
        if (routerInfo != null) {
            return routerInfo;
        } else {
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
                            log.error(ExceptionUtil.getMessage(e));
                        }
                    }

                }
                return router.get(pattern.getOrgUrl());
            }
        }
        return null;
    }


    public static RouterPermission getRouterPermission(String url, HttpMethod requestType) {
        Map<String, RouterPermission> stringRouterPermissionMap = routerPermission(requestType);
        if (stringRouterPermissionMap == null) {
            return null;
        }
        RouterPermission routerPermission = stringRouterPermissionMap.get(url);
        if (routerPermission != null) {
            return routerPermission;
        } else {
            PatternUri pattern = isPattern(url, requestType);
            if (pattern != null) {
                return stringRouterPermissionMap.get(pattern.getOrgUrl());
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
            if (stringRouterPermissionMap != null) {
                stringRouterPermissionMap.forEach((a, b) -> permissions.add(b));
            }
        }
        return permissions;
    }
}
