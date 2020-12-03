package net.hserver;

import top.hserver.HServerApplication;
import top.hserver.core.ioc.annotation.Controller;
import top.hserver.core.ioc.annotation.HServerBoot;

/**
 * @author hxm
 */
@HServerBoot
public class App {

    public static void main(String[] args) {
        HServerApplication.hotUpdate();
        HServerApplication.run(App.class, 8888, args);
    }
}
