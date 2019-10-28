package com.test;

import com.hserver.HServerApplication;
import com.hserver.core.server.context.StaticFile;
import com.hserver.core.server.handlers.StaticHandler;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

public class WebApp {
    public static void main(String[] args) {
        HServerApplication.run(WebApp.class);
    }
}
