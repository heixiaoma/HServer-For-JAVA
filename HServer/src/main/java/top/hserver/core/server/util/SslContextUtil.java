package top.hserver.core.server.util;

import io.netty.handler.ssl.*;
import top.hserver.core.server.HServer;
import top.hserver.core.server.context.ConstConfig;

import java.io.File;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static io.netty.handler.codec.http2.Http2SecurityUtil.CIPHERS;

public class SslContextUtil {
    /*
     * List of ALPN/NPN protocols in order of preference. MICRO_EXP_VERSION
     * requires that HTTP2_VERSION be present and that MICRO_EXP_VERSION should be
     * preferenced over HTTP2_VERSION.
     */
    static final List<String> NEXT_PROTOCOL_VERSIONS =
            Collections.unmodifiableList(Arrays.asList(ApplicationProtocolNames.HTTP_2, ApplicationProtocolNames.HTTP_1_1));

    /*
     * These configs use ACCEPT due to limited support in OpenSSL.  Actual protocol enforcement is
     * done in ProtocolNegotiators.
     */
    private static final ApplicationProtocolConfig ALPN = new ApplicationProtocolConfig(
            ApplicationProtocolConfig.Protocol.ALPN,
            ApplicationProtocolConfig.SelectorFailureBehavior.NO_ADVERTISE,
            ApplicationProtocolConfig.SelectedListenerFailureBehavior.ACCEPT,
            NEXT_PROTOCOL_VERSIONS);

    private static final ApplicationProtocolConfig NPN = new ApplicationProtocolConfig(
            ApplicationProtocolConfig.Protocol.NPN,
            ApplicationProtocolConfig.SelectorFailureBehavior.NO_ADVERTISE,
            ApplicationProtocolConfig.SelectedListenerFailureBehavior.ACCEPT,
            NEXT_PROTOCOL_VERSIONS);

    private static final ApplicationProtocolConfig NPN_AND_ALPN = new ApplicationProtocolConfig(
            ApplicationProtocolConfig.Protocol.NPN_AND_ALPN,
            ApplicationProtocolConfig.SelectorFailureBehavior.NO_ADVERTISE,
            ApplicationProtocolConfig.SelectedListenerFailureBehavior.ACCEPT,
            NEXT_PROTOCOL_VERSIONS);


    private static SslProvider defaultSslProvider() {
        return OpenSsl.isAvailable() ? SslProvider.OPENSSL : SslProvider.JDK;
    }

    private static ApplicationProtocolConfig selectApplicationProtocolConfig(SslProvider provider) {
        switch (provider) {
            case JDK: {
                if (JettyTlsUtil.isJettyAlpnConfigured()) {
                    return ALPN;
                }
                if (JettyTlsUtil.isJettyNpnConfigured()) {
                    return NPN;
                }
                throw new IllegalArgumentException("Jetty ALPN/NPN has not been properly configured.");
            }
            case OPENSSL: {
                if (!OpenSsl.isAvailable()) {
                    throw new IllegalArgumentException("OpenSSL is not installed on the system.");
                }
                if (OpenSsl.isAlpnSupported()) {
                    return NPN_AND_ALPN;
                } else {
                    return NPN;
                }
            }
            default:
                throw new IllegalArgumentException("Unsupported provider: " + provider);
        }
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
                if (ConstConfig.openHttp2) {
                    sslContext.ciphers(CIPHERS, SupportedCipherSuiteFilter.INSTANCE);
                    sslContext.applicationProtocolConfig((selectApplicationProtocolConfig(defaultSslProvider())));
                }
                ConstConfig.sslContext = sslContext.build();
                return;
            }

            //看看是不是resources里面的
            InputStream cinput = HServer.class.getResourceAsStream("/ssl/" + certFilePath);
            InputStream pinput = HServer.class.getResourceAsStream("/ssl/" + privateKeyPath);

            if (cinput != null && pinput != null) {
                SslContextBuilder sslContext = SslContextBuilder.forServer(cinput, pinput, privateKeyPwd).sslProvider(defaultSslProvider());
                if (ConstConfig.openHttp2) {
                    sslContext.ciphers(CIPHERS, SupportedCipherSuiteFilter.INSTANCE);
                    sslContext.applicationProtocolConfig((selectApplicationProtocolConfig(defaultSslProvider())));
                }
                ConstConfig.sslContext = sslContext.build();
                cinput.close();
                pinput.close();
            }
        } catch (Exception s) {
            s.printStackTrace();
        }
    }
}
