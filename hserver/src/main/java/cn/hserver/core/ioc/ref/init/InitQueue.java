package cn.hserver.core.ioc.ref.init;

import cn.hserver.core.ioc.ref.PackageScanner;
import cn.hserver.core.queue.QueueDispatcher;

import java.io.IOException;
import java.util.Set;

public class InitQueue extends Init{

    public InitQueue(Set<String> packages) {
        super(packages);
    }

    @Override
    public void init(PackageScanner scanner) throws IOException {
        QueueDispatcher.init(scanner);
    }
}
