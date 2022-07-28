package cn.hserver.plugin.web.handlers;


import cn.hserver.core.server.context.ConstConfig;
import io.netty.handler.codec.http.HttpResponseStatus;
import cn.hserver.plugin.web.context.StaticFile;
import cn.hserver.plugin.web.context.HServerContext;
import cn.hserver.plugin.web.exception.BusinessException;

import java.io.*;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.Enumeration;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * 静态文件的处理，包括文件缓存效果等
 *
 * @author hxm
 */

public class StaticHandler {

    private final static String BASE = "static";
    private final static String BASE_PATH = "/" + BASE;

    /**
     * 此处的静态文件缓存是非常有必要的，直接拉低了整体QPS.
     */
    private final static CopyOnWriteArraySet<String> STATIC_FILE_URI = new CopyOnWriteArraySet<>();

    static {
        /**
         * 把静态文件递归遍历出来.
         */
        //jar的
        if (ConstConfig.RUNJAR) {
            onlineFile(ConstConfig.CLASSPATH);
        } else {
            //开发中的.
            String s = ConstConfig.CLASSPATH + BASE_PATH;
            developFile(s,s.length());
        }
    }

    public StaticFile handler(String uri, HServerContext hServerContext) {
        //判断一次文件是否有/index.html文件
        if (uri.endsWith("/")) {
            uri += "index.html";
        }
        if (!STATIC_FILE_URI.contains(uri)){
            return null;
        }
        InputStream input = StaticHandler.class.getResourceAsStream(BASE_PATH + uri);
        if (input != null) {
            return buildStaticFile(input, uri, hServerContext);
        }
        return null;
    }

    /**
     * 构建一个静态文件对象
     *
     * @param input
     * @param url
     * @return
     */
    private StaticFile buildStaticFile(InputStream input, String url, HServerContext hServerContext) {
        StaticFile staticFile;
        try {
            //获取文件大小
            int available = input.available();
            staticFile = new StaticFile();
            staticFile.setSize(available);
            //获取文件名
            int i = url.lastIndexOf("/");
            int i1 = url.lastIndexOf(".");
            if (i > -1 && i1 > 0) {
                String fileName = url.substring(i + 1, url.length());
                String[] split = fileName.split("\\.");
                staticFile.setFileName(fileName);
                //设置文件是下载还
                staticFile.setFileType(split[split.length - 1]);
            } else {
                return null;
            }
            staticFile.setInputStream(input);
        } catch (Exception e) {
            throw new BusinessException(HttpResponseStatus.INTERNAL_SERVER_ERROR.code(), "获取文件大小异常", e, hServerContext.getWebkit());
        }
        return staticFile;
    }


    private static void developFile(String path,int length) {
        File file = new File(path);
        if (file.exists()) {
            File[] files = file.listFiles();
            if (null != files) {
                for (File file2 : files) {
                    if (file2.isDirectory()) {
                        developFile(file2.getAbsolutePath(),length);
                    } else {
                        try {
                            URL url = new URL("file:" + file2.getAbsolutePath());
                            STATIC_FILE_URI.add(url.getPath().substring(length));
                        }catch (Exception ignored){}
                    }
                }
            }
        }
    }

    public static void onlineFile(String path) {
        try {
            JarURLConnection jarURLConnection = (JarURLConnection) new URL(path).openConnection();
            JarFile jarFile = jarURLConnection.getJarFile();
            Enumeration<JarEntry> entry = jarFile.entries();
            while (entry.hasMoreElements()) {
                JarEntry jar = entry.nextElement();
                String name = jar.getName();
                if (name.contains(BASE) && !name.endsWith("/")) {
                    STATIC_FILE_URI.add(name.substring(BASE.length()));
                }
            }
            jarFile.close();
        } catch (Exception ignored) {
        }
    }
}
