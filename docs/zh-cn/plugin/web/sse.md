# SSE 使用
```java
   @GET("/sse")
    public void res(HttpResponse response) throws Exception {
        SSeStream sSeStream = response.getSSeStream();
        for (int i = 0; i < 100; i++) {
            sSeStream.sendSseEvent(new SSeEvent.Builder().id(String.valueOf(i)).event("a").data(String.valueOf(i)).build());
            //模拟延迟
            Thread.sleep(1000);
        }
    }
```