package cn.hserver.core.ioc;


import cn.hserver.core.boot.HServerApplication;
import cn.hserver.core.config.ConfigData;
import cn.hserver.core.context.IocApplicationContext;
import cn.hserver.core.logging.HServerLogConfig;
import cn.hserver.core.scheduling.TaskManager;
import cn.hserver.core.util.EnvironmentUtil;

public class Main {
    public static void main(String[] args) throws Exception {
        HServerApplication.run(Main.class);
        System.out.println("HServer <UNK>");
    }
}
