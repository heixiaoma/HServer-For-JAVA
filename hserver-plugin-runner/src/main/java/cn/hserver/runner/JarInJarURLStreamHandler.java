package cn.hserver.runner;

import java.io.IOException;
import java.net.URL;
public class JarInJarURLStreamHandler extends java.net.URLStreamHandler {
	private ClassLoader classLoader;

	public JarInJarURLStreamHandler(ClassLoader classLoader) {
    	this.classLoader = classLoader;
	}
	protected java.net.URLConnection openConnection(URL u) throws IOException {
    	return new JarInJarURLConnection(u, classLoader);
    }
}
