package cn.hserver.cloud.common;

public class RegisterConfig extends CloudAddress{
    //注册名字
    private String registerName;
    //注册我的Ip
    private String registerMyIp;
    //注册我的端口
    private Integer registerMyPort;
    //注册分组
    private String registerGroupName = "DEFAULT_GROUP";

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

    public String getRegisterGroupName() {
        return registerGroupName;
    }

    public void setRegisterGroupName(String groupName) {
        this.registerGroupName = groupName;
    }

    public boolean hasNull() {
        return this.getCloudAddress() == null || this.registerMyIp == null || this.registerMyPort == null || this.registerName == null;
    }
}
