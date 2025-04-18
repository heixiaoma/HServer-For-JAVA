package cn.hserver.plugin.web.context.sse;

public class SSeEvent {

    private String id;

    private String event;

    private String data;

    private int retry;

    public SSeEvent() {}

    public SSeEvent(Builder builder){
        this.id = builder.id;
        this.event = builder.event;
        this.data = builder.data;
        this.retry = builder.retry;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public int getRetry() {
        return retry;
    }

    public void setRetry(int retry) {
        this.retry = retry;
    }

    public static class Builder {
        private String id;

        private String event;

        private String data;

        private int retry;

        // Setter 方法返回 Builder 本身，方便链式调用
        public Builder id(String id) {
            this.id = id;
            return this;
        }

        public Builder event(String event) {
            this.event = event;
            return this;
        }

        public Builder data(String data) {
            this.data = data;
            return this;
        }

        public Builder retry(int retry) {
            this.retry = retry;
            return this;
        }

        public SSeEvent build() {
            return new SSeEvent(this);
        }
    }

}
