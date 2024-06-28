package cn.hserver.plugin.beetlsql.hook;

import io.netty.util.concurrent.FastThreadLocal;
import cn.hserver.plugin.beetlsql.tx.Tx;
import org.beetl.sql.core.DSTransactionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import cn.hserver.core.interfaces.HookAdapter;
import cn.hserver.core.ioc.annotation.Hook;

import java.lang.reflect.Method;
import java.sql.SQLException;

/**
 * @author hxm
 */
@Hook(Tx.class)
public class TxHook implements HookAdapter {
    private static final Logger logger = LoggerFactory.getLogger(TxHook.class);
    private static final FastThreadLocal<Long> TIMEOUT_MILLISECOND = new FastThreadLocal<>();

    @Override
    public void before(Class aClass, Method method, Object[] objects) {
        Tx annotation = method.getAnnotation(Tx.class);
        if (annotation!=null) {
            DSTransactionManager.start();
            if (annotation.timeoutMillisecond() != -1) {
                TIMEOUT_MILLISECOND.set(System.currentTimeMillis());
            }
        }
    }

    @Override
    public Object after(Class aClass, Method method, Object o) {
        try {
            Tx annotation = method.getAnnotation(Tx.class);
            if (annotation != null) {
                try {
                    if (annotation.timeoutMillisecond() != -1) {
                        Long startTime = TIMEOUT_MILLISECOND.get();
                        if (System.currentTimeMillis() - startTime > annotation.timeoutMillisecond()) {
                            //超时了进行回滚
                            try {
                                DSTransactionManager.rollback();
                            } catch (SQLException ex) {
                                logger.error(ex.getMessage(),ex);
                            }
                        }
                    }
                    DSTransactionManager.commit();
                } catch (SQLException e) {
                    try {
                        DSTransactionManager.rollback();
                    } catch (SQLException ex) {
                        logger.error(ex.getMessage(),ex);
                    }
                }
            }
            return o;
        }finally {
            TIMEOUT_MILLISECOND.remove();
        }
    }

    @Override
    public void throwable(Class aClass, Method method, Throwable throwable) {
        try {
            Tx annotation = method.getAnnotation(Tx.class);
            if (annotation != null) {
                try {
                    //看看是否制定了回滚异常类型
                    Class<? extends Throwable>[] classes = annotation.rollbackFor();
                    if (classes.length != 0) {
                        for (Class<? extends Throwable> aClass1 : classes) {
                            if (throwable.getClass() == aClass1) {
                                DSTransactionManager.rollback();
                                return;
                            }
                        }
                        DSTransactionManager.commit();
                        return;
                    }
                    DSTransactionManager.rollback();
                } catch (SQLException e) {
                    logger.error(e.getMessage(),e);
                }
            }
        }finally {
            TIMEOUT_MILLISECOND.remove();
        }
    }
}
