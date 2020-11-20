package top.hserver.core.ioc.ref;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.List;

/**
 * @author hxm
 */
public interface PackageScanner {

    /**
     * 获取列表
     * @param annotation
     * @param <A>
     * @return
     * @throws IOException
     */
    <A extends Annotation> List<Class<?>> getAnnotationList(Class<A> annotation) throws IOException;
}
