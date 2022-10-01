package cn.hserver.plugin.v8.utils;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

public class FileUtils {
    public static File createTemporaryScriptFile(String script, String name) throws IOException {
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
