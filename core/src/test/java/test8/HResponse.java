package test8;

import top.hserver.core.server.context.HeadMap;

public interface HResponse {

    int getStatusCode();

    HeadMap getHeader();

    byte[] getBody();

    String getBodyAsString();

    Throwable getException();

    interface Listener {
        void complete(HResponse arg);

        void exception(Throwable t);
    }
}
