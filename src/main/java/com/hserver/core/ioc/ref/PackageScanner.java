package com.hserver.core.ioc.ref;

import java.io.IOException;
import java.util.List;

public interface PackageScanner {
    List<String> getBeansPackage() throws IOException;

    List<String> getControllersPackage() throws IOException;

    List<String> getHooksPackage() throws IOException;
}
