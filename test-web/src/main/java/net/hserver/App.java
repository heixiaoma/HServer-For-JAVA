package net.hserver;

import top.hserver.HServerApplication;

/**
 * @author hxm
 */
public class App {

    public static void main(String[] args) {
        HServerApplication.run(App.class, 8888, args);
    }
}
