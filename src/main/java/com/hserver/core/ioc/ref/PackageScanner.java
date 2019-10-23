package com.hserver.core.ioc.ref;

import java.io.IOException;
import java.util.List;

public interface PackageScanner {
    List<String> getBeansPackage() throws IOException;

    List<String> getActionsPackage() throws IOException;
}
