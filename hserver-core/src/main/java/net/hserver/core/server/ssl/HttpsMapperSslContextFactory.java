package net.hserver.core.server.ssl;


import javax.net.ssl.SSLContext;

/**
 * @author hxm
 */
public final class HttpsMapperSslContextFactory {

    private static final String PROTOCOL = "TLS";
    private static final SSLContext CLIENT_CONTEXT;

    static {

        SSLContext  clientContext;

        try {
            clientContext = SSLContext.getInstance(PROTOCOL);
            clientContext.init(null, HttpsMapperTrustManagerFactory.getTrustManagers(), null);
        } catch (Exception e) {
            throw new Error(
                    "Failed to initialize the client-side SSLContext", e);
        }

        CLIENT_CONTEXT = clientContext;
    }

    public static SSLContext getClientContext() {
        return CLIENT_CONTEXT;
    }

    private HttpsMapperSslContextFactory() {
        // Unused
    }
}