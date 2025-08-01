package cn.hserver.netty.web.context;

import cn.hserver.mvc.request.Cookie;

public class DefaultCookie implements Cookie {

    private final io.netty.handler.codec.http.cookie.Cookie nettyCookie;

    public DefaultCookie(io.netty.handler.codec.http.cookie.Cookie nettyCookie) {
        this.nettyCookie = nettyCookie;
    }

    public DefaultCookie(String name, String value) {
        this.nettyCookie = new io.netty.handler.codec.http.cookie.DefaultCookie(name, value);
    }

    @Override
    public String name() {
        return nettyCookie.name();
    }

    @Override
    public String value() {
        return nettyCookie.value();
    }

    @Override
    public void setValue(String var1) {
        nettyCookie.setValue(var1);
    }

    @Override
    public boolean wrap() {
        return nettyCookie.wrap();
    }

    @Override
    public void setWrap(boolean var1) {
        nettyCookie.setWrap(var1);
    }

    @Override
    public String domain() {
        return nettyCookie.domain();
    }

    @Override
    public void setDomain(String var1) {
        nettyCookie.setDomain(var1);
    }

    @Override
    public String path() {
        return nettyCookie.path();
    }

    @Override
    public void setPath(String var1) {
        nettyCookie.setPath(var1);
    }

    @Override
    public long maxAge() {
        return nettyCookie.maxAge();
    }

    @Override
    public void setMaxAge(long var1) {
        nettyCookie.setMaxAge(var1);
    }

    @Override
    public boolean isSecure() {
        return nettyCookie.isSecure();
    }

    @Override
    public void setSecure(boolean var1) {
        nettyCookie.setSecure(var1);
    }

    @Override
    public boolean isHttpOnly() {
        return nettyCookie.isHttpOnly();
    }

    @Override
    public void setHttpOnly(boolean var1) {
        nettyCookie.setHttpOnly(var1);
    }
}
