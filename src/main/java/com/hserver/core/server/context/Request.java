package com.hserver.core.server.context;

import com.hserver.core.ioc.annotation.GET;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Request {
    private String uri;

}
