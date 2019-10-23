package com.test;

import com.hserver.HServerApplication;
import com.hserver.core.ioc.IocUtil;
import com.test.bean.Test;

public class WebApp {
    public static void main(String[] args) {
        HServerApplication.run(WebApp.class);
        Test bean = IocUtil.getBean(Test.class);
        bean.show();
    }
}
