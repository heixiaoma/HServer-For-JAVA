package cn.hserver.runner;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.nio.file.Files;
import java.nio.file.Paths;

public class JarURLConnection extends URLConnection {
    private final ClassLoader classLoader;
    private final boolean inJar;

    public JarURLConnection(URL url, ClassLoader classLoader, boolean inJar) {
        super(url);
        this.classLoader = classLoader;
        this.inJar = inJar;
    }

    public void connect() throws IOException {
    }

    public InputStream getInputStream() throws IOException {
        if (inJar) {
            String file = URLDecoder.decode(url.getFile(), "UTF-8");
            InputStream result = classLoader.getResourceAsStream(file);
            if (result == null) {
                throw new MalformedURLException("Could not open InputStream for URL '" + url + "'");
            }
            if (Runner.password != null && !Runner.password.trim().isEmpty()) {
                try {
                    return AesUtil.decrypt(result, Runner.password);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
            return result;
        } else {
            if (Files.exists(Paths.get(url.getFile()))) {
                if (Runner.password != null && !Runner.password.trim().isEmpty()) {
                    try {
                        return AesUtil.decrypt(Files.newInputStream(Paths.get(url.getFile())),Runner.password);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }else {
                    return Files.newInputStream(Paths.get(url.getFile()));
                }
            }
        }
        return null;
    }
}
