package cn.hserver.cloud;

public class DiscoveryInfo {
    private String serverName;
    private DynamicRoundRobin dynamicRoundRobin;

    public DiscoveryInfo() {
    }

    public String getServerName() {
        return serverName;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    public DynamicRoundRobin getDynamicRoundRobin() {
        return dynamicRoundRobin;
    }

    public void setDynamicRoundRobin(DynamicRoundRobin dynamicRoundRobin) {
        this.dynamicRoundRobin = dynamicRoundRobin;
    }

    public DiscoveryInfo(String serverName, DynamicRoundRobin dynamicRoundRobin) {
        this.serverName = serverName;
        this.dynamicRoundRobin = dynamicRoundRobin;
    }
}
