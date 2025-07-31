package cn.hserver.core.ioc;

import cn.hserver.core.boot.HServerApplication;
import cn.hserver.core.config.ConstConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {
    private static final Logger log = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) throws Exception {
        HServerApplication.run(Main.class);
        log.info("{}",ConstConfig.CLASSPATH);
        System.out.println("HServer <UNK>");
    }
}
