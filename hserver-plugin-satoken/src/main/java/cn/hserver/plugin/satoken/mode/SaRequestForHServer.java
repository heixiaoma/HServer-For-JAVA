package cn.hserver.plugin.satoken.mode;

import cn.dev33.satoken.SaManager;
import cn.dev33.satoken.application.ApplicationInfo;
import cn.dev33.satoken.context.model.SaRequest;
import cn.dev33.satoken.exception.SaTokenException;
import cn.dev33.satoken.util.SaFoxUtil;
import cn.hserver.plugin.web.interfaces.HttpRequest;
import cn.hserver.plugin.web.interfaces.HttpResponse;
import io.netty.handler.codec.http.cookie.Cookie;

import java.io.IOException;
import java.util.*;

public class SaRequestForHServer implements SaRequest {

    /**
     * 底层Request对象
     */
    protected HttpRequest request;

    /**
     * 实例化
     *
     * @param request request对象
     */
    public SaRequestForHServer(HttpRequest request) {
        this.request = request;
    }

    /**
     * 获取底层源对象
     */
    @Override
    public Object getSource() {
        return request;
    }

    /**
     * 在 [请求体] 里获取一个值
     */
    @Override
    public String getParam(String name) {
        return request.query(name);
    }

    /**
     * 获取 [请求体] 里提交的所有参数名称
     *
     * @return 参数名称列表
     */
    @Override
    public List<String> getParamNames() {
        return new ArrayList<>(request.getRequestParams().keySet());
    }

    /**
     * 获取 [请求体] 里提交的所有参数
     *
     * @return 参数列表
     */
    @Override
    public Map<String, String> getParamMap() {
        // 获取所有参数
        Map<String, List<String>> parameterMap = request.getRequestParams();
        Map<String, String> map = new LinkedHashMap<>(parameterMap.size());
        for (String key : parameterMap.keySet()) {
            List<String> values = parameterMap.get(key);
            map.put(key, values.get(0));
        }
        return map;
    }

    /**
     * 在 [请求头] 里获取一个值
     */
    @Override
    public String getHeader(String name) {
        return request.getHeader(name);
    }

    /**
     * 在 [Cookie作用域] 里获取一个值
     */
    @Override
    public String getCookieValue(String name) {
        Set<Cookie> cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie != null && name.equals(cookie.name())) {
                    return cookie.value();
                }
            }
        }
        return null;
    }

    /**
     * 返回当前请求path (不包括上下文名称)
     */
    @Override
    public String getRequestPath() {
        return ApplicationInfo.cutPathPrefix(request.getUri());
    }

    /**
     * 返回当前请求的url，例：http://xxx.com/test
     *
     * @return see note
     */
    public String getUrl() {
        String currDomain = SaManager.getConfig().getCurrDomain();
        if (!SaFoxUtil.isEmpty(currDomain)) {
            return currDomain + this.getRequestPath();
        }
        return request.getUri();
    }

    /**
     * 返回当前请求的类型
     */
    @Override
    public String getMethod() {
        return request.getRequestType().name();
    }

    /**
     * 转发请求
     */
    @Override
    public Object forward(String path) {
        try {
            HttpResponse response = (HttpResponse) SaManager.getSaTokenContextOrSecond().getResponse().getSource();
            response.redirect(path);
            return null;
        } catch (Exception e) {
            throw new SaTokenException(e);
        }
    }

}
