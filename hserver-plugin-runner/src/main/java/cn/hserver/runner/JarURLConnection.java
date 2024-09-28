package cn.hserver.runner;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;

public class JarURLConnection extends URLConnection {
    private final ClassLoader classLoader;
    private final boolean inJar;
    private final boolean encrypt;

    public JarURLConnection(URL url, ClassLoader classLoader, boolean inJar,boolean encrypt) {
        super(url);
        this.classLoader = classLoader;
        this.inJar = inJar;
        this.encrypt = encrypt;
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
            return result;
        } else {
            return null;
        }
    }
}
