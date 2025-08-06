package cn.hserver.plugin.satoken.config;

import cn.dev33.satoken.annotation.*;
import cn.dev33.satoken.apikey.annotation.SaCheckApiKey;
import cn.dev33.satoken.oauth2.annotation.SaCheckAccessToken;
import cn.dev33.satoken.oauth2.annotation.SaCheckClientIdSecret;
import cn.dev33.satoken.oauth2.annotation.SaCheckClientToken;
import cn.dev33.satoken.sign.annotation.SaCheckSign;
import cn.dev33.satoken.strategy.SaAnnotationStrategy;
import cn.hserver.core.aop.HookAdapter;
import cn.hserver.core.aop.annotation.Hook;

import java.lang.reflect.Method;

@Hook(value = {
        SaCheckDisable.class,
        SaCheckHttpBasic.class,
        SaCheckHttpDigest.class,
        SaCheckLogin.class,
        SaCheckOr.class,
        SaCheckPermission.class,
        SaCheckRole.class,
        SaCheckSafe.class,
        SaIgnore.class,
        SaCheckAccessToken.class,
        SaCheckClientIdSecret.class,
        SaCheckClientToken.class,
        SaCheckApiKey.class,
        SaCheckSign.class,
})
public class SaAnnotationInterceptor implements HookAdapter {

    @Override
    public void before(Class clazz, Method method, Object[] args) throws Throwable {
        SaAnnotationStrategy.instance.checkMethodAnnotation.accept(method);
    }

    @Override
    public Object after(Class clazz, Method method, Object object) {
        return object;
    }

    @Override
    public void throwable(Class clazz, Method method, Throwable throwable) {
        throw new RuntimeException(throwable);
    }
}