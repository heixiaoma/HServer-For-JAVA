package cn.hserver.plugin.cloud;

public class RpcServer {

    private String ip;

    private Integer port;

    private String serverName;

    private String groupName;

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public String getServerName() {
        return serverName;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    @Override
    public String toString() {
        return "RpcServer{" +
                "ip='" + ip + '\'' +
                ", port=" + port +
                ", serverName='" + serverName + '\'' +
                '}';
    }
}
