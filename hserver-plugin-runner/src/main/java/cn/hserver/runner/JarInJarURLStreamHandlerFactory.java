package cn.hserver.runner;

import java.net.URLStreamHandler;
import java.net.URLStreamHandlerFactory;

public class JarInJarURLStreamHandlerFactory implements URLStreamHandlerFactory {
	private ClassLoader classLoader;
	private URLStreamHandlerFactory chainFac;

	public JarInJarURLStreamHandlerFactory(ClassLoader cl) {
		this.classLoader = cl;
	}
	public URLStreamHandler createURLStreamHandler(String protocol) {
		if (protocol.equals("jarinjar")) {
			return new JarInJarURLStreamHandler(classLoader);
		}
		if (chainFac != null)
			return chainFac.createURLStreamHandler(protocol);
		return null;
	}
	public void setURLStreamHandlerFactory(URLStreamHandlerFactory fac) {
		chainFac = fac;
	}


}
