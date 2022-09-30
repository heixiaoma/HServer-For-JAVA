package cn.hserver.plugin.v8;

import com.eclipsesource.v8.*;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;


public class Test {
    static String NODE_SCRIPT = "var http = require('http');\n"
            + ""
            + "var server = http.createServer(function (request, response) {\n"
            + " response.writeHead(200, {'Content-Type': 'text/plain'});\n"
            + " response.end(someJavaMethod());\n"
            + "});\n"
            + ""
            + "server.listen(8000);\n"
            + "console.log('Server running at https://127.0.0.1:8000/');";
    public static void main(String[] args) throws IOException {
        final NodeJS nodeJS = NodeJS.createNodeJS();
        JavaCallback callback = new JavaCallback() {

            public Object invoke(V8Object receiver, V8Array parameters) {
                return "Hello, JavaWorld!";
            }
        };

        nodeJS.getRuntime().registerJavaMethod(callback, "someJavaMethod");
        File nodeScript =  createTemporaryScriptFile(NODE_SCRIPT, "example");

        nodeJS.exec(nodeScript);

        while(nodeJS.isRunning()) {
            nodeJS.handleMessage();
        }
        nodeJS.release();
    }

    private static File createTemporaryScriptFile(String script, String name) throws IOException {
        File tempFile = File.createTempFile(name, ".js.tmp");
        PrintWriter writer = new PrintWriter(tempFile, "UTF-8");

        try {
            writer.print(script);
        } finally {
            writer.close();
        }

        return tempFile;
    }
}
