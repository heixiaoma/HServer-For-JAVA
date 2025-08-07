package cn.hserver.mvc.util;

import cn.hserver.mvc.constants.WebConstConfig;
import cn.hserver.mvc.server.SslData;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;


public class SslUtil {

    public static SslData loadSSlData() {
        try {
            if (WebConstConfig.SSL_KEY == null || WebConstConfig.SSL_CERT == null) {
                return null;
            }
            InputStream keyInputStream = Files.newInputStream(Paths.get(WebConstConfig.SSL_KEY));
            InputStream certInputStream = Files.newInputStream(Paths.get(WebConstConfig.SSL_CERT));
            return new SslData(keyInputStream, certInputStream);
        } catch (Exception ignored) {
            //看看是不是resources里面的
            InputStream keyInputStream = SslUtil.class.getResourceAsStream("/ssl/" + WebConstConfig.SSL_KEY);
            InputStream certInputStream = SslUtil.class.getResourceAsStream("/ssl/" + WebConstConfig.SSL_CERT);
            if (keyInputStream != null && certInputStream != null) {
                return new SslData(keyInputStream, certInputStream);
            }
        }
        return null;
    }
}
