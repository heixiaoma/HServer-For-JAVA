package cn.hserver.runner;

import java.io.IOException;
import java.net.URL;

public class JarURLStreamHandler extends java.net.URLStreamHandler {
	private final ClassLoader classLoader;
	private final boolean inJar;

	public JarURLStreamHandler(ClassLoader classLoader,boolean inJar) {
    	this.classLoader = classLoader;
		this.inJar=inJar;
	}
	protected java.net.URLConnection openConnection(URL u) throws IOException {
    	return new JarURLConnection(u, classLoader,inJar);
    }
}
