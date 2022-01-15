package top.hserver.core.server.context;

public class HumMessage {

    private HumMessageType humMessageType = HumMessageType.USER;

    private Object data;

    public HumMessage() {
    }

    public HumMessage(Object data) {
        this.data = data;
    }

    public HumMessageType getHumMessageType() {
        return humMessageType;
    }

    public void setHumMessageType(HumMessageType humMessageType) {
        this.humMessageType = humMessageType;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "HumMessage{" +
                "humMessageType=" + humMessageType +
                ", data=" + data +
                '}';
    }
}
