package cn.hserver.plugin.web.context;

import cn.hserver.core.ioc.annotation.ConfigurationProperties;

@ConfigurationProperties(prefix = "web")
public class WebConfig {

    private Long readLimit;
    private Long writeLimit;
    private Integer httpContentSize;
    private Integer maxWebsocketFrameLength;
    private Integer businessPool;

    private String rootPath;
    private String certPath;
    private String privateKeyPath;
    private String privateKeyPwd;


    public Long getReadLimit() {
        return readLimit;
    }

    public void setReadLimit(Long readLimit) {
        this.readLimit = readLimit;
    }

    public Long getWriteLimit() {
        return writeLimit;
    }

    public void setWriteLimit(Long writeLimit) {
        this.writeLimit = writeLimit;
    }

    public Integer getHttpContentSize() {
        return httpContentSize;
    }

    public void setHttpContentSize(Integer httpContentSize) {
        this.httpContentSize = httpContentSize;
    }

    public Integer getMaxWebsocketFrameLength() {
        return maxWebsocketFrameLength;
    }

    public void setMaxWebsocketFrameLength(Integer maxWebsocketFrameLength) {
        this.maxWebsocketFrameLength = maxWebsocketFrameLength;
    }

    public Integer getBusinessPool() {
        return businessPool;
    }

    public void setBusinessPool(Integer businessPool) {
        this.businessPool = businessPool;
    }

    public String getRootPath() {
        return rootPath;
    }

    public void setRootPath(String rootPath) {
        this.rootPath = rootPath;
    }

    public String getCertPath() {
        return certPath;
    }

    public void setCertPath(String certPath) {
        this.certPath = certPath;
    }

    public String getPrivateKeyPath() {
        return privateKeyPath;
    }

    public void setPrivateKeyPath(String privateKeyPath) {
        this.privateKeyPath = privateKeyPath;
    }

    public String getPrivateKeyPwd() {
        return privateKeyPwd;
    }

    public void setPrivateKeyPwd(String privateKeyPwd) {
        this.privateKeyPwd = privateKeyPwd;
    }
}