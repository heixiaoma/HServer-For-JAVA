package top.hserver.core.server.context;

import lombok.Data;

@Data
public class PatternUri {

    private String key;

    private String orgUrl;

    private String patternUrl;

    public PatternUri(String key, String orgUrl, String patternUrl) {
        this.key = key;
        this.orgUrl = orgUrl;
        this.patternUrl = patternUrl;
    }
}
