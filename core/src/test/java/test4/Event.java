package test4;

public class Event {

    public Object data;

    private long sequence;


    public Event() {
    }

    public Object getData() {
        return data;
    }

    public long getSequence() {
        return sequence;
    }

    public void setSequence(long sequence) {
        this.sequence = sequence;
    }

    public void setData(Object data) {
        this.data = data;
    }
}