package cn.hserver.mvc.sse;


public abstract class SSeStream {
    public SSeStream(Integer retryMilliseconds) {
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

     public abstract SSeStream sendSseEvent(String event) ;
     public abstract void sendStartHeader();
     public abstract SSeStream addCloseListener(Runnable runnable);
     public abstract void sendRetryEvent(String event) ;


}
