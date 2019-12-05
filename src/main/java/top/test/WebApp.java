package top.test;

import top.hserver.HServerApplication;

public class WebApp {
    public static void main(String[] args) {
        HServerApplication.run(WebApp.class, 8888);
    }
}
