package cn.hserver.cloud.common;

public class DisProp {
    //注册名字
    private String discoveryAddress;

    public DisProp() {

    }

    public DisProp(String discoveryAddress) {
        this.discoveryAddress = discoveryAddress;
    }


    public String getDiscoveryAddress() {
        return discoveryAddress;
    }

    public void setDiscoveryAddress(String discoveryAddress) {
        this.discoveryAddress = discoveryAddress;
    }

    public boolean hasNull() {
        if (this.discoveryAddress == null ) {
            return true;
        } else {
            return false;
        }
    }
}
