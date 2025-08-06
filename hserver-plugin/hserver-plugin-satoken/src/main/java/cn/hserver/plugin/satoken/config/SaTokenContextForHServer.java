package cn.hserver.plugin.satoken.config;

import cn.dev33.satoken.context.SaTokenContextForReadOnly;
import cn.dev33.satoken.context.model.SaRequest;
import cn.dev33.satoken.context.model.SaResponse;
import cn.dev33.satoken.context.model.SaStorage;
import cn.hserver.mvc.context.WebContextHolder;
import cn.hserver.plugin.satoken.mode.SaRequestForHServer;
import cn.hserver.plugin.satoken.mode.SaResponseForHServer;
import cn.hserver.plugin.satoken.mode.SaStorageForHServer;

public class SaTokenContextForHServer implements SaTokenContextForReadOnly {
    @Override
    public boolean isValid() {
        return WebContextHolder.getWebContext()!=null;
    }

    @Override
    public SaRequest getRequest() {
        return new SaRequestForHServer(WebContextHolder.getWebContext().request);
    }

    @Override
    public SaResponse getResponse() {
        return new SaResponseForHServer(WebContextHolder.getWebContext().response);
    }

    @Override
    public SaStorage getStorage() {
        return  new SaStorageForHServer(WebContextHolder.getWebContext().request);
    }

}
