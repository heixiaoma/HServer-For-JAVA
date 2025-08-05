package cn.hserver.mvc.staticfile;

import cn.hserver.core.config.ConstConfig;
import cn.hserver.core.util.JarInputStreamUtil;
import cn.hserver.mvc.constants.WebConstConfig;
import cn.hserver.mvc.context.WebContext;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

public class StaticFileHandler {

    private final  String BASE = "static";
    private final  String BASE_PATH = "/" + BASE;

   private final Set<String> STATIC_FILE_URI = new HashSet<>();

    public StaticFileHandler() {
        if (WebConstConfig.STATIC_PATH == null) {
            if (ConstConfig.RUN_JAR) {
                onlineFile(ConstConfig.CLASSPATH);
            } else {
                //开发中的.
                String s = ConstConfig.CLASSPATH + BASE_PATH;
                developFile(s,s.length());
            }
        }else {
            developFile(WebConstConfig.STATIC_PATH,new File(WebConstConfig.STATIC_PATH).getAbsolutePath().length());
        }
    }


    public void handlerStatic(WebContext webContext) {
        if (STATIC_FILE_URI.isEmpty()) {
            return;
        }
        String uri = webContext.request.getUri();
        //判断一次文件是否有/index.html文件
            if (uri.endsWith("/")) {
                uri += "index.html";
            }
            if (!STATIC_FILE_URI.contains(uri)){
                return;
            }
            if (WebConstConfig.STATIC_PATH==null){
                InputStream input = StaticFileHandler.class.getResourceAsStream(BASE_PATH + uri);
                webContext.response.downloadStream(input,getFileNameFromUrl(uri));
            }else {
                webContext.response.downloadFile(new File(WebConstConfig.STATIC_PATH,uri));
            }
    }


    private  String getFileNameFromUrl(String path) {
        try {
            // 截取最后一个"/"后面的内容
            int lastSlashIndex = path.lastIndexOf('/');
            if (lastSlashIndex >= 0 && lastSlashIndex < path.length() - 1) {
                String fileName = path.substring(lastSlashIndex + 1);
                // 去除可能的查询参数
                int queryIndex = fileName.indexOf('?');
                if (queryIndex != -1) {
                    fileName = fileName.substring(0, queryIndex);
                }
                return fileName;
            }
        } catch (Throwable ignored) {
        }
        return null;
    }

    private  void developFile(String path,int length) {
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

    private  void onlineFile(String path) {
        try {
            InputStream resourceAsStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(path);
            if (resourceAsStream != null) {
                try (JarInputStream jarInputStream = new JarInputStream(JarInputStreamUtil.decrypt(resourceAsStream))) {
                    JarEntry jarEntry;
                    while ((jarEntry = jarInputStream.getNextJarEntry()) != null) {
                        String name = jarEntry.getName();
                        if (name.contains(BASE) && !name.endsWith("/")) {
                            STATIC_FILE_URI.add(name.substring(BASE.length()));
                        }
                    }
                } catch (IOException ignored) {
                }
            }
        } catch (Exception ignored) {
        }
    }

}
