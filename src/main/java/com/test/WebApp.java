package com.test;

import com.hserver.core.server.context.StaticFile;
import com.hserver.core.server.handlers.StaticHandler;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

public class WebApp {
    public static void main(String[] args) {
//        HServerApplication.run(WebApp.class);
        StaticFile handler = new StaticHandler().handler("a.txt");

        System.out.println(getInputString(handler.getInputStream()));
    }
    private static String getInputString(InputStream inputStream) {
        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String s; // 依次循环，至到读的值为空
            StringBuilder sb = new StringBuilder();
            while ((s = bufferedReader.readLine()) != null) {
                sb.append(s);
            }
            return sb.toString();
        } catch (Exception e) {
            return null;
        }
    }

}
