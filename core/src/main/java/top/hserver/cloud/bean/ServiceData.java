package top.hserver.cloud.bean;

import lombok.Data;

import java.net.InetSocketAddress;

/**
 * @author hxm
 */
@Data
public class ServiceData {

    private String serverName;

    private String host;

    private Integer port;

    public InetSocketAddress getInetSocketAddress() {
        return new InetSocketAddress(host, port);
    }

}
