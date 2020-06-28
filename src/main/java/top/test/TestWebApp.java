package top.test;

import top.hserver.HServerApplication;

public class TestWebApp {
    public static void main(String[] args) {
        HServerApplication.run(TestWebApp.class, 8888,args);
    }
}
