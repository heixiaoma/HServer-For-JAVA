package top.hserver;

import top.hserver.cloud.CloudManager;
import top.hserver.core.ioc.ref.InitBean;
import top.hserver.core.properties.PropertiesInit;
import top.hserver.core.server.HServer;
import lombok.extern.slf4j.Slf4j;

import static top.hserver.core.event.EventDispatcher.startTaskThread;


@Slf4j
public class HServerApplication {

    public static void run(Class classz, Integer port, String ...args) {
        if (classz == null || port == null) {
            log.info("HServer 启动失败");
            return;
        }
        log.info("初始化配置文件：{}", classz.getName());
        PropertiesInit.init();
        log.info("初始化配置完成：{}", classz.getName());
        log.info("HServer 启动中....");
        log.info("Package 扫描中");
        InitBean.init(HServerApplication.class);
        InitBean.init(classz);
        log.info("IOC 装配中：{}", classz.getName());
        InitBean.injection();
        //Beetlsql注入
        InitBean.BeetlSqlinit();
        log.info("IOC 全部装配完成：{}", classz.getName());
        //云启动
        CloudManager.run();
        try {
            new HServer(port,args).run();
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }
}
