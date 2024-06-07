package cn.hserver.core.ioc.ref.init;

import cn.hserver.core.ioc.ref.PackageScanner;
import cn.hserver.core.plugs.PlugsManager;

import java.util.Set;

public class InitPlugin extends Init{
    public InitPlugin(Set<String> packages) {
        super(packages);
    }

    @Override
    public void init(PackageScanner scanner) {

        //插件初始
        PlugsManager.getPlugin().iocInit(scanner);
    }
}
