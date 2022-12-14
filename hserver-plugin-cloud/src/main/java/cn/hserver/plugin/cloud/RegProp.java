package cn.hserver.plugin.cloud;

import cn.hserver.core.ioc.annotation.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.cloud.reg")
public class RegProp {
    //注册名字
    private String registerAddress;
    //注册名字
    private String registerName;
    //注册我的Ip
    private String registerMyIp;
    //注册我的端口
    private Integer registerMyPort;
    //注册分组
    private String groupName = "DEFAULT_GROUP";

    public String getRegisterAddress() {
        return registerAddress;
    }

    public void setRegisterAddress(String registerAddress) {
        this.registerAddress = registerAddress;
    }

    public String getRegisterName() {
        return registerName;
    }

    public void setRegisterName(String registerName) {
        this.registerName = registerName;
    }

    public String getRegisterMyIp() {
        return registerMyIp;
    }

    public void setRegisterMyIp(String registerMyIp) {
        this.registerMyIp = registerMyIp;
    }

    public Integer getRegisterMyPort() {
        return registerMyPort;
    }

    public void setRegisterMyPort(Integer registerMyPort) {
        this.registerMyPort = registerMyPort;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }
}
