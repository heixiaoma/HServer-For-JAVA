package cn.hserver.core.ioc.ref.init;

import cn.hserver.core.ioc.ref.PackageScanner;

import java.util.Set;

public abstract class Init {
    protected final Set<String> packages;

    public Init(Set<String> packages) {
        this.packages = packages;
    }

    protected abstract void init(PackageScanner scanner) throws Exception;
}
