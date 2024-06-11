package cn.hserver.plugin.satoken.config;

import cn.dev33.satoken.annotation.*;
import cn.dev33.satoken.strategy.SaStrategy;
import cn.hserver.core.interfaces.HookAdapter;
import cn.hserver.core.ioc.annotation.Hook;
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
})
public class SaAnnotationInterceptor implements HookAdapter {

    @Override
    public void before(Class clazz, Method method, Object[] args) throws Throwable {
        SaStrategy.instance.checkMethodAnnotation.accept(method);
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
