package cn.hserver.mvc.server;


import java.io.InputStream;

public class SslData {

    private final InputStream keyInputStream;
    private final InputStream certInputStream;

    public SslData(InputStream keyInputStream, InputStream certInputStream) {
        this.keyInputStream = keyInputStream;
        this.certInputStream = certInputStream;
    }

    public InputStream getCertInputStream() {
        return certInputStream;
    }

    public InputStream getKeyInputStream() {
        return keyInputStream;
    }
}
