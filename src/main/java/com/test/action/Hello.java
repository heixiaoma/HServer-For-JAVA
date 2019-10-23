package com.test.action;

import com.hserver.core.ioc.annotation.Action;
import com.hserver.core.ioc.annotation.GET;
import com.hserver.core.ioc.annotation.In;
import com.test.bean.TestService;

@Action
public class Hello {

    @In
    private TestService testService;


    @GET("/hello")
    public String index() {
        return testService.test();
    }
}
