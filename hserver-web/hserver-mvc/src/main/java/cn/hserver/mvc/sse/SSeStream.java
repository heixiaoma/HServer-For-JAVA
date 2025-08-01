package cn.hserver.mvc.sse;


import com.sun.org.slf4j.internal.Logger;
import com.sun.org.slf4j.internal.LoggerFactory;

public class SSeStream {
    private static final Logger log = LoggerFactory.getLogger(SSeStream.class);
//    private final Channel channel;
//    private final HeadMap headMap;

   // public SSeStream(Integer retryMilliseconds, HeadMap headMap) {
//        this.channel = HServerContextHolder.getWebKit().httpRequest.getCtx().channel();
//        this.headMap = headMap;
//        sendStartHeader();
//        if (retryMilliseconds != null && retryMilliseconds > 0) {
//            sendRetryEvent(retryMilliseconds);
//        }
  //  }

//    public SSeStream addCloseListener(Runnable runnable) {
//        channel.closeFuture().addListener(future -> {
//            runnable.run();
//        });
//        return this;
//    }
//
//    public SSeStream sendSseEvent(SSeEvent sSeEvent) {
//        String message;
//        if (StringUtil.isNullOrEmpty(sSeEvent.getEvent())) {
//            message = "data: " + sSeEvent.getData() + "\n\n";
//        } else if (sSeEvent.getId() == null) {
//            message = "event: " + sSeEvent.getEvent() + "\n" +
//                    "data: " + sSeEvent.getData() + "\n\n";
//        } else {
//            message = "id: " + sSeEvent.getId() + "\n" +
//                    "event: " + sSeEvent.getEvent() + "\n" +
//                    "data: " + sSeEvent.getData() + "\n\n";
//        }
//        channel.writeAndFlush(new DefaultHttpContent(Unpooled.wrappedBuffer(message.getBytes(StandardCharsets.UTF_8)))).addListener(future -> {
//            if (!future.isSuccess()) {
//                log.error("sendSseEvent error", future.cause());
//            }else {
//                log.debug(message.trim());
//            }
//        });
//        return this;
//    }
//
//    private void sendStartHeader() {
//        HttpResponse response = new DefaultHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
//        headMap.forEach((k, v) -> response.headers().add(k, v));
//        response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/event-stream");
//        response.headers().set(HttpHeaderNames.CACHE_CONTROL, "no-cache");
//        response.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
//        channel.writeAndFlush(response).addListener(future -> {
//            if (!future.isSuccess()) {
//                log.error("sendSseEvent error", future.cause());
//            }
//        });
//    }
//
//    private void sendRetryEvent(Integer retryMilliseconds) {
//        ByteBuf byteBuf = Unpooled.wrappedBuffer(("retry: " + retryMilliseconds + "\n\n").getBytes(StandardCharsets.UTF_8));
//        channel.writeAndFlush(new DefaultHttpContent(byteBuf)).addListener(future -> {
//            if (!future.isSuccess()) {
//                log.error("sendSseEvent error", future.cause());
//            }
//        });
//    }

}
