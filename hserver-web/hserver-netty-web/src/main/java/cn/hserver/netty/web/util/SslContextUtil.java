package cn.hserver.netty.web.util;

import cn.hserver.mvc.server.SslData;
import cn.hserver.netty.web.constants.NettyConfig;
import io.netty.handler.ssl.OpenSsl;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.SslProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class SslContextUtil {

    private static final Logger log = LoggerFactory.getLogger(SslContextUtil.class);

    private static SslProvider defaultSslProvider() {
        log.debug("SSL:{}",OpenSsl.isAvailable() ? SslProvider.OPENSSL : SslProvider.JDK);
        return OpenSsl.isAvailable() ? SslProvider.OPENSSL : SslProvider.JDK;
    }

    public static boolean initSsl(int sslPort,SslData sslData) {
        try {
            if (sslData == null||sslPort<=0) {
                return false;
            }
            if (sslData.getCertInputStream() != null && sslData.getKeyInputStream() != null) {
                SslContextBuilder sslContext = SslContextBuilder.forServer(sslData.getCertInputStream(), sslData.getKeyInputStream()).sslProvider(defaultSslProvider());
                NettyConfig.SSL_CONTEXT = sslContext.build();
                NettyConfig.SSL_PORT = sslPort;
                return true;
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return false;
        }finally {
            if (sslData != null) {
                if (sslData.getCertInputStream() != null) {
                    try {
                        sslData.getCertInputStream().close();
                    } catch (IOException ignored) {
                    }
                }
                if (sslData.getKeyInputStream() != null) {
                    try {
                        sslData.getKeyInputStream().close();
                    } catch (IOException ignored) {
                    }
                }
            }
        }
        return false;
    }
}
