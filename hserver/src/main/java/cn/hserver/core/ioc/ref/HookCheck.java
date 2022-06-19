package cn.hserver.core.ioc.ref;

public class HookCheck {

    private String iocName;

    private boolean isList;

    public HookCheck(String iocName, boolean isList) {
        this.iocName = iocName;
        this.isList = isList;
    }

    public String getIocName() {
        return iocName;
    }

    public void setIocName(String iocName) {
        this.iocName = iocName;
    }

    public boolean isList() {
        return isList;
    }

    public void setList(boolean list) {
        isList = list;
    }
}
