package top.hserver.core.interfaces;

import top.hserver.core.server.context.Webkit;

public interface GlobalException {
    void handler(Exception exception, Webkit webkit);
}
