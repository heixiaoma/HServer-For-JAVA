package cn.hserver.runner;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

public class JarInJarURLConnection extends URLConnection {
    private ClassLoader classLoader;

    public JarInJarURLConnection(URL url, ClassLoader classLoader) {
        super(url);
        this.classLoader = classLoader;
    }

    public void connect() throws IOException {
    }

    public InputStream getInputStream() throws IOException {
            InputStream result = null;
            JarInputStream jarInputStream = null;
            try {
                String[] jarInPath = getJarInPath(url.getPath());
                if (jarInPath==null){
                    return new ByteArrayInputStream(new byte[]{});
                }
                result = classLoader.getResourceAsStream(jarInPath[0]);
                jarInputStream = new JarInputStream(result);
                JarEntry entry;
                while ((entry = jarInputStream.getNextJarEntry()) != null) {
                    if (entry.getName().equals(jarInPath[1])) {
                        byte[] buffer = new byte[1024];
                        int bytesRead;
                        // 读取 entry 的内容并存储到 ByteArrayOutputStream 中
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        while ((bytesRead = jarInputStream.read(buffer)) != -1) {
                            baos.write(buffer, 0, bytesRead);
                        }
                        // 将 ByteArrayOutputStream 转换成 ByteArrayInputStream 并返回
                        return new ByteArrayInputStream(baos.toByteArray());
                    }
                }
            } finally {
                if (jarInputStream != null) {
                    try {
                        jarInputStream.close();
                    } catch (IOException e) {
                        // 可以记录异常信息或者忽略
                    }
                }
                if (result != null) {
                    try {
                        result.close();
                    } catch (IOException e) {
                        // 可以记录异常信息或者忽略
                    }
                }
            }
            return new ByteArrayInputStream(new byte[]{});
    }

    private String[] getJarInPath(String path) {
        System.out.println("解析："+path);
        String[] paths = new String[2];
        String[] split = path.split("!/");
        if (split.length == 3) {
            paths[0] = split[1];
            paths[1] = split[2];
            return paths;
        } else {
            System.err.println(path);
            return null;
        }
    }


}
