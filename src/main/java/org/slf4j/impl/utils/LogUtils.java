package org.slf4j.impl.utils;

import org.slf4j.impl.Ansi;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;



public class LogUtils {

    private static final DateTimeFormatter d1 = DateTimeFormatter.ofPattern("yyyyMMdd");
    private static final DateTimeFormatter d2 = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter d3 = DateTimeFormatter.ofPattern("HHmmss");

    private static Map<String, String> THREAD_NAME_CACHE = new HashMap<>();
    private static Map<String, String> CLASS_NAME_CACHE  = new HashMap<>();

    private static boolean isWindows;

    static {
        isWindows = System.getProperties().getProperty("os.name").toLowerCase().contains("win");
    }

    public static boolean isWindows() {
        return isWindows;
    }

    public static boolean isEmpty(String str) {
        return null == str || str.length() == 0;
    }

    public static boolean isNotEmpty(String value) {
        return null != value && !value.isEmpty();
    }

    public static String getDate() {
        return LocalDate.now().format(d1);
    }

    public static String getNormalDate() {
        return LocalDate.now().format(d2);
    }

    public static String getTime() {
        return LocalTime.now().format(d3);
    }

    public static String padLeft(String s, int n) {
        return String.format("%1$" + n + "s", s);
    }

    public static String getColorShortName(String className) {
        if (CLASS_NAME_CACHE.containsKey(className)) {
            return CLASS_NAME_CACHE.get(className);
        }
        int           len       = 31;
        StringBuilder shortName = buildShortName(className);
        String        val       = padLeft(shortName.toString(), len) + " : ";
        val = Ansi.Blue.format(val);
        CLASS_NAME_CACHE.put(className, val);
        return val;
    }

    public static String getShortName(String className) {
        if (CLASS_NAME_CACHE.containsKey(className)) {
            return CLASS_NAME_CACHE.get(className);
        }
        int           len       = 31;
        StringBuilder shortName = buildShortName(className);
        String        val       = padLeft(shortName.toString(), len);
        val = val + " : ";
        CLASS_NAME_CACHE.put(className, val);
        return val;
    }

    private static StringBuilder buildShortName(String className) {
        String[]      packageNames = className.split("\\.");
        StringBuilder shortName    = new StringBuilder();
        int           pos          = 0;
        for (String pkg : packageNames) {
            if (pos != packageNames.length - 1) {
                shortName.append(pkg.charAt(0)).append('.');
            } else {
                shortName.append(pkg);
            }
            pos++;
        }
        return shortName;
    }

    public static String getColorThreadPadding() {
        String key = Thread.currentThread().getName();
        if (THREAD_NAME_CACHE.containsKey(key)) {
            return THREAD_NAME_CACHE.get(key);
        }
        String val = "[ " + padLeft(Thread.currentThread().getName(), 17) + " ] ";
        val = Ansi.White.format(val);
        THREAD_NAME_CACHE.put(key, val);
        return val;
    }

    public static String getThreadPadding() {
        String key = Thread.currentThread().getName();
        if (THREAD_NAME_CACHE.containsKey(key)) {
            return THREAD_NAME_CACHE.get(key);
        }
        String val = "[ " + padLeft(Thread.currentThread().getName(), 17) + " ] ";
        THREAD_NAME_CACHE.put(key, val);
        return val;
    }

    public static void sleep(long ms) {
        try {
            TimeUnit.MILLISECONDS.sleep(ms);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static String stackTraceToString(Throwable e) {
        StringWriter sw = new StringWriter();
        PrintWriter  pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        return sw.toString();
    }

}
