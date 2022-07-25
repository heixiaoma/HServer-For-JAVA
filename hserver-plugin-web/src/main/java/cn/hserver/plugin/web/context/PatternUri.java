package cn.hserver.plugin.web.context;


import java.util.List;

/**
 * @author hxm
 */
public class PatternUri {

    /**
     * 方法参数KEY
     */
    private List<String> keys;

    /**
     * 正则URL
     */
    private String orgUrl;

    /**
     * 匹配的URL传递的真实URL
     */
    private String patternUrl;

    /**
     * http请求类型
     */
    private String requestType;

    public String getRequestType() {
        return requestType;
    }

    public void setRequestType(String requestType) {
        this.requestType = requestType;
    }

    public List<String> getKeys() {
        return keys;
    }

    public void setKeys(List<String> keys) {
        this.keys = keys;
    }

    public String getOrgUrl() {
        return orgUrl;
    }

    public void setOrgUrl(String orgUrl) {
        this.orgUrl = orgUrl;
    }

    public String getPatternUrl() {
        return patternUrl;
    }

    public void setPatternUrl(String patternUrl) {
        this.patternUrl = patternUrl;
    }

    public PatternUri(List<String> keys, String orgUrl, String patternUrl,String requestType) {
        this.keys = keys;
        this.orgUrl = orgUrl;
        this.patternUrl = patternUrl;
        this.requestType=requestType;
    }
}
