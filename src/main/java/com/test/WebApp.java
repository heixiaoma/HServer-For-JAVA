package com.test;

import com.hserver.HServerApplication;

public class WebApp {
    public static void main(String[] args) {
        HServerApplication.run(WebApp.class, 8080);
    }
}
