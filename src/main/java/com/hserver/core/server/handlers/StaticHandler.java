package com.hserver.core.server.handlers;


import com.hserver.core.server.context.StaticFile;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * 静态文件的处理，包括文件缓存效果等
 */

@Slf4j
public class StaticHandler {

    private String basePath = "/static/";

    public StaticFile handler(String uri) {

        //jar环境还是开发环境;
        boolean isJar = true;

        // jar file
        if (isJar) {
            InputStream input = getResourceStreamFromJar(basePath + uri);
            if (input != null) {
                return buildStaticFile(input, uri);
            }
            return null;
        }

        // no jar file
        if (!isJar) {
            InputStream input = StaticHandler.class.getResourceAsStream(basePath + uri);
            if (input != null) {
                return buildStaticFile(input, uri);
            }
            return null;
        }
        return null;
    }



    private InputStream getResourceStreamFromJar(String uri) {
        return StaticHandler.class.getResourceAsStream("/META-INF/resources" + uri);
    }

    /**
     * 构建一个静态文件对象
     *
     * @param inputStream
     * @return
     */
    private StaticFile buildStaticFile(InputStream inputStream, String url) {
        StaticFile staticFile = new StaticFile();

        try {
            //获取文件大小
            int available = inputStream.available();
            staticFile.setSize(available);
            //获取文件名
            int i = url.lastIndexOf("/");
            if (i > 0) {
                staticFile.setFileName(url.substring(i + 1, url.length()));
            }
            staticFile.setFileType(true);
            staticFile.setInputStream(inputStream);

        } catch (Exception e) {
            log.error("获取文件大小异常:" + e.getMessage());
            try {
                inputStream.close();
            } catch (Exception e1) {
                log.error("关闭文件流异常:" + e.getMessage());
            }
        }
        return staticFile;

    }
}
