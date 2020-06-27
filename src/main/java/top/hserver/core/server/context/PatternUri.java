package top.hserver.core.server.context;

import lombok.Data;

import java.util.List;

/**
 * @author hxm
 */
@Data
public class PatternUri {

    private List<String> keys;

    private String orgUrl;

    private String patternUrl;

    public PatternUri(List<String> keys, String orgUrl, String patternUrl) {
        this.keys = keys;
        this.orgUrl = orgUrl;
        this.patternUrl = patternUrl;
    }
}
