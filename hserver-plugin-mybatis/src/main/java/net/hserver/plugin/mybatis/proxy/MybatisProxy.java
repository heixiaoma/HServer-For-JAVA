package net.hserver.plugin.mybatis.proxy;

import io.netty.util.concurrent.FastThreadLocal;
import javassist.util.proxy.ProxyFactory;
import javassist.util.proxy.ProxyObject;
import net.hserver.plugin.mybatis.hook.TxHook;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;

/**
 * @author hxm
 */
public class MybatisProxy {
    private static final Logger log = LoggerFactory.getLogger(MybatisProxy.class);

    private static final FastThreadLocal<SqlSession> sqlSessionFastThreadLocal = new FastThreadLocal<>();

    public static SqlSession get() {
        return sqlSessionFastThreadLocal.get();
    }

    private MybatisProxy() {
    }

    private static MybatisProxy mybatisProxy;

    public static MybatisProxy getInstance() {
        if (mybatisProxy == null) {
            mybatisProxy = new MybatisProxy();
        }
        return mybatisProxy;
    }

    public Object getProxy(Class clazz, SqlSessionFactory sqlSessionFactory) throws InstantiationException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        // 代理工厂
        ProxyFactory proxyFactory = new ProxyFactory();
        // 设置需要创建子类的父类
        if (clazz.isInterface()) {
            proxyFactory.setInterfaces(new Class[]{clazz});
        }
        Object o1 = proxyFactory.create(null, null);
        ((ProxyObject) o1).setHandler((self, thisMethod, proceed, args) -> {
            //代理调用
            if (sqlSessionFastThreadLocal.get() == null) {
                SqlSession sqlSession = sqlSessionFactory.openSession();
                sqlSessionFastThreadLocal.set(sqlSession);
            }
            Object invoke = null;
            try {
                Object mapper = sqlSessionFastThreadLocal.get().getMapper(clazz);
                thisMethod.setAccessible(true);
                invoke = thisMethod.invoke(mapper, args);
                /**
                 *  未开启事务时 自己提交，开启了，交给TXHook处理
                 */
                if (TxHook.ISTX.get() == null || !TxHook.ISTX.get()) {
                    sqlSessionFastThreadLocal.get().commit();
                }
            } catch (Throwable throwable) {
                /**
                 *  未开启事务时 自己回滚，开启了，交给TXHook处理
                 */
                if (TxHook.ISTX.get() == null || !TxHook.ISTX.get()) {
                    sqlSessionFastThreadLocal.get().rollback();
                    log.debug("rollback：" + throwable.getMessage());
                    throw new Exception(throwable);
                } else {
                    throw new Exception(throwable);
                }
            } finally {
                //如果开启了自定义事务，那么就不关闭了
                if (TxHook.ISTX.get() == null || !TxHook.ISTX.get()) {
                    sqlSessionFastThreadLocal.get().close();
                    removeSession();
                }
            }
            return invoke;
        });
        return o1;
    }

    public static void removeSession() {
        sqlSessionFastThreadLocal.remove();
    }
}
