package net.hserver.core.ioc.annotation;

/**
 * @author hxm
 */

public enum RequestMethod {
    GET,
    HEAD,
    POST,
    PUT,
    PATCH,
    DELETE,
    OPTIONS,
    CONNECT,
    TRACE;

    private RequestMethod() {
    }

    public static String[] getRequestMethodAll(){
      return new String[]{
        "GET",
        "HEAD",
        "POST",
        "PUT",
        "PATCH",
        "DELETE",
        "OPTIONS",
        "CONNECT",
        "TRACE"
      };
    }
}