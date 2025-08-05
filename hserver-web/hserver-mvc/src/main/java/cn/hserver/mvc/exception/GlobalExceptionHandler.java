package cn.hserver.mvc.exception;

import cn.hserver.mvc.context.WebContext;

public abstract class GlobalExceptionHandler {

    public abstract void handlerException(Throwable throwable, WebContext webContext);

    protected <T> T getException(Throwable throwable,Class<T> clazz){
        // 如果传入的异常为null，直接返回null
        if (throwable == null) {
            return null;
        }
        // 如果当前异常就是目标类型，直接返回
        if (clazz.isInstance(throwable)) {
            @SuppressWarnings("unchecked")
            T result = (T) throwable;
            return result;
        }
        // 递归查找嵌套的异常（通过getCause()方法）
        Throwable cause = throwable.getCause();
        if (cause != null) {
            return getException(cause, clazz);
        }
        // 整个异常链中都没有找到目标类型的异常
        return null;
    }

}
