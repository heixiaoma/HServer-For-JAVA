package top.hserver.core.ioc.ref;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.List;

public interface PackageScanner {
    <A extends Annotation> List<Class<?>> getAnnotationList( Class<A> annotation) throws IOException;
}
