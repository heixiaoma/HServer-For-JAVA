package top.hserver.core.ioc.ref;

import java.io.IOException;
import java.util.List;

public interface PackageScanner {
    List<Class<?>> getBeansPackage() throws IOException;

    List<Class<?>> getControllersPackage() throws IOException;

    List<Class<?>> getHooksPackage() throws IOException;

    List<Class<?>> getFiltersPackage() throws IOException;

    List<Class<?>> getWebSocketPackage() throws IOException;

}
