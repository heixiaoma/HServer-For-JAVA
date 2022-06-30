package cn.hserver.core.server.util;

import io.netty.handler.ssl.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import cn.hserver.core.server.HServer;
import cn.hserver.core.server.context.ConstConfig;

import java.io.File;
import java.io.InputStream;

import static io.netty.handler.codec.http2.Http2SecurityUtil.CIPHERS;

public class SslContextUtil {

    private static final Logger log = LoggerFactory.getLogger(SslContextUtil.class);

    private static SslProvider defaultSslProvider() {
        log.debug("SSL:{}",OpenSsl.isAvailable() ? SslProvider.OPENSSL : SslProvider.JDK);
        return OpenSsl.isAvailable() ? SslProvider.OPENSSL : SslProvider.JDK;
    }

    public static void setSsl() {
        PropUtil instance = PropUtil.getInstance();
        String certFilePath = instance.get("certPath");
        String privateKeyPath = instance.get("privateKeyPath");
        String privateKeyPwd = instance.get("privateKeyPwd");
        if (privateKeyPath == null || certFilePath == null || privateKeyPath.trim().length() == 0 || certFilePath.trim().length() == 0) {
            return;
        }
        try {
            //检查下是不是外部路径。
            File cfile = new File(certFilePath);
            File pfile = new File(privateKeyPath);
            if (cfile.isFile() && pfile.isFile()) {
                SslContextBuilder sslContext = SslContextBuilder.forServer(cfile, pfile, privateKeyPwd).sslProvider(defaultSslProvider());
                ConstConfig.sslContext = sslContext.build();
                return;
            }

            //看看是不是resources里面的
            InputStream cinput = HServer.class.getResourceAsStream("/ssl/" + certFilePath);
            InputStream pinput = HServer.class.getResourceAsStream("/ssl/" + privateKeyPath);

            if (cinput != null && pinput != null) {
                SslContextBuilder sslContext = SslContextBuilder.forServer(cinput, pinput, privateKeyPwd).sslProvider(defaultSslProvider());
                ConstConfig.sslContext = sslContext.build();
                cinput.close();
                pinput.close();
            }
        } catch (Exception s) {
            log.error(ExceptionUtil.getMessage(s));
        }
    }
}
