package cn.hserver.plugin.satoken.config;

import cn.dev33.satoken.context.SaTokenContextForReadOnly;
import cn.dev33.satoken.context.model.SaRequest;
import cn.dev33.satoken.context.model.SaResponse;
import cn.dev33.satoken.context.model.SaStorage;
import cn.hserver.plugin.satoken.mode.SaRequestForHServer;
import cn.hserver.plugin.satoken.mode.SaResponseForHServer;
import cn.hserver.plugin.satoken.mode.SaStorageForHServer;
import cn.hserver.plugin.web.context.HServerContextHolder;

public class SaTokenContextForHServer implements SaTokenContextForReadOnly {
    @Override
    public boolean isValid() {
        return HServerContextHolder.getWebKit()!=null;
    }

    @Override
    public SaRequest getRequest() {
        return new SaRequestForHServer(HServerContextHolder.getWebKit().httpRequest);
    }

    @Override
    public SaResponse getResponse() {
        return new SaResponseForHServer(HServerContextHolder.getWebKit().httpResponse);
    }

    @Override
    public SaStorage getStorage() {
        return  new SaStorageForHServer(HServerContextHolder.getWebKit().httpRequest);
    }

}
