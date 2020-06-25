package top.hserver.core.server.exception;

import lombok.Builder;
import lombok.Data;
import top.hserver.core.server.context.ConstConfig;

/**
 * @author hxm
 */
@Data
@Builder
public class BusinessBean {

    private String version;

    private Integer code;

    private String method;

    private String url;

    private String args;

    private String errorMsg;

    private String errorDesc;

    private String bugAddress;

    private String communityAddress;

}
