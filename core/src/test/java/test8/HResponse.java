package test8;

import io.netty.handler.codec.http.HttpHeaders;

public interface HResponse {

    int getStatusCode();

    HttpHeaders getHeader();

    byte[] getBody();

    String getBodyAsString();

    Throwable getException();

    interface Listener {
        void complete(HResponse arg);

        void exception(Throwable t);
    }
}
