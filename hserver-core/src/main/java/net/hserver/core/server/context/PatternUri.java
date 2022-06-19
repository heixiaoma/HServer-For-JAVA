package net.hserver.core.server.context;


import java.util.List;

/**
 * @author hxm
 */
public class PatternUri {

    private List<String> keys;

    private String orgUrl;

    private String patternUrl;


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

    public PatternUri(List<String> keys, String orgUrl, String patternUrl) {
        this.keys = keys;
        this.orgUrl = orgUrl;
        this.patternUrl = patternUrl;
    }
}
