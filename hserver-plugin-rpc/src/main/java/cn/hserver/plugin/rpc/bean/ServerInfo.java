package cn.hserver.plugin.rpc.bean;

public class ServerInfo {
    private String serverName;
    private String groupName;

    public ServerInfo() {
    }

    public ServerInfo(String serverName, String groupName) {
        if (groupName == null) {
            groupName = "DEFAULT_GROUP";
        }
        this.serverName = serverName;
        this.groupName = groupName;
    }

    public String getServerName() {
        return serverName;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }
}
