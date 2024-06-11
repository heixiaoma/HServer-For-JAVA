package cn.hserver.plugin.satoken.config;

import cn.dev33.satoken.context.SaTokenContext;
import cn.dev33.satoken.context.model.SaRequest;
import cn.dev33.satoken.context.model.SaResponse;
import cn.dev33.satoken.context.model.SaStorage;
import cn.hserver.plugin.satoken.mode.SaRequestForHServer;
import cn.hserver.plugin.satoken.mode.SaResponseForHServer;
import cn.hserver.plugin.satoken.mode.SaStorageForHServer;
import cn.hserver.plugin.web.context.HServerContextHolder;

public class SaTokenContextForHServer implements SaTokenContext {

    /**
     * 获取当前请求的Request对象
     */
    @Override
    public SaRequest getRequest() {
        return new SaRequestForHServer(HServerContextHolder.getWebKit().httpRequest);
    }

    /**
     * 获取当前请求的Response对象
     */
    @Override
    public SaResponse getResponse() {
        return new SaResponseForHServer(HServerContextHolder.getWebKit().httpResponse);
    }

    /**
     * 获取当前请求的 [存储器] 对象
     */
    @Override
    public SaStorage getStorage() {
        return new SaStorageForHServer(HServerContextHolder.getWebKit().httpRequest);
    }

    /**
     * 校验指定路由匹配符是否可以匹配成功指定路径
     */
    @Override
    public boolean matchPath(String pattern, String path) {
        return path.matches(pattern);
    }

}
