package net.hserver;

import top.hserver.HServerApplication;

public class StartApp {
    public static void main(String[] args) {
        HServerApplication.run(StartApp.class, 8888, args);
    }
}
