package com.hserver;

import com.hserver.core.ioc.IocUtil;
import com.hserver.core.ioc.ref.InitBean;
import com.hserver.core.server.HServer;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

@Slf4j
public class HServerApplication {

    public static void run(Class classz) {
        log.info("HServer 启动中....", classz.getName());
        log.info("Package 扫描中", classz.getName());
        InitBean.init(HServerApplication.class);
        InitBean.init(classz);
        log.info("IOC 装配中", classz.getName());
        InitBean.injection();
        log.info("IOC 全部装配完成", classz.getName());
        try {
            new HServer(8081).run();
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }
}
