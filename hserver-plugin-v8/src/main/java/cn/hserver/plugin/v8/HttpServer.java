package cn.hserver.plugin.v8;

import com.eclipsesource.v8.*;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

public class HttpServer {

	private static V8 v8;
	private static NodeJS nodeJS;

	public static void main(String[] args) throws IOException {
		nodeJS = NodeJS.createNodeJS();
		v8 = nodeJS.getRuntime();
		JSObject http = createHttpServer(nodeJS);
		System.out.println(http);
		JSObject server = http.execute("createServer", f((V8Object receiver, V8Array parameters) -> {
			JSObject response = jsObject((V8Object) parameters.get(1));
			V8Object params = o("Content-Type", "text/plain");
			response.execute("writeHead", 200, params);
			response.execute("end", "Hello, from the JavaWorld!");
			response.release();
			params.release();
			return null;
		} ));

		server.execute("listen", 8000);
		System.out.println("Node HTTP server listening on port 8000.");

		server.release();
		http.release();
		while (nodeJS.isRunning()) {
			nodeJS.handleMessage();
		}
		nodeJS.release();
	}

	private static JSObject createHttpServer(NodeJS node) throws IOException {
		V8Object exports = node.require(createTemporaryScriptFile("var http = require('http'); module.exports = {'http' : http};", "httpStartup"));
		try {
			return new JSObject((V8Object) exports.get("http"));
		} finally {
			exports.release();
		}
	}

	static class JSObject {
		private V8Object object;

		public JSObject(V8Object object) {
			this.object = object;
		}

		public JSObject execute(String function, Object... parameters) {
			Object result = object.executeJSFunction(function, parameters);
			if (result instanceof V8Object) {
				return new JSObject((V8Object) result);
			}
			return null;
		}

		@Override
		public String toString() {
			return object.toString();
		}

		public void release() {
			this.object.release();
			this.object = null;
		}
	}

	public static V8Function f(JavaCallback callback) {
		return new V8Function(v8, callback);
	}

	public static V8Object o(String k, String v) {
		return new V8Object(v8).add(k, v);
	}

	public static V8Object o(String k, int v) {
		return new V8Object(v8).add(k, v);
	}

	public static JSObject jsObject(V8Object object) {
		return new JSObject(object);
	}

	private static File createTemporaryScriptFile(final String script, final String name) throws IOException {
		File tempFile = File.createTempFile(name, ".js");
		PrintWriter writer = new PrintWriter(tempFile, "UTF-8");
		try {
			writer.print(script);
		} finally {
			writer.close();
		}
		return tempFile;
	}
}