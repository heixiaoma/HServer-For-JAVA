package cn.hserver.mvc.sse;


public abstract class SSeStream {
    private final Integer retryMilliseconds;
    public SSeStream(Integer retryMilliseconds) {
        this.retryMilliseconds = retryMilliseconds;
    }

    protected void init(){
        this.sendStartHeader();
        if (retryMilliseconds != null&&retryMilliseconds > 0) {
            sendRetryEvent(("retry: " + retryMilliseconds + "\n\n"));
        }
    }

     public  SSeStream sendSseEvent(SSeEvent event) {
         String message;
        if (event.getEvent()==null||event.getEvent().isEmpty()) {
            message = "data: " + event.getData() + "\n\n";
        } else if (event.getId() == null) {
            message = "event: " + event.getEvent() + "\n" +
                    "data: " + event.getData() + "\n\n";
        } else {
            message = "id: " + event.getId() + "\n" +
                    "event: " + event.getEvent() + "\n" +
                    "data: " + event.getData() + "\n\n";
        }
        sendSseEvent(message);
        return this;
     }

     protected abstract SSeStream sendSseEvent(String event) ;
     protected abstract void sendStartHeader();
     public abstract SSeStream addCloseListener(Runnable runnable);
     protected abstract void sendRetryEvent(String event) ;
     public abstract void close();
}
