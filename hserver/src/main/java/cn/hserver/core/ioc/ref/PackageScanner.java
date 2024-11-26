package cn.hserver.core.ioc.ref;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.Set;

/**
 * @author hxm
 */
public interface PackageScanner extends AutoCloseable {

    /**
     * 获取列表
     * @param annotation
     * @param <A>
     * @return
     * @throws IOException
     */
    <A extends Annotation> Set<Class<?>> getAnnotationList(Class<A> annotation) throws IOException;
}
