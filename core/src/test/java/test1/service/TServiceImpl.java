package test1.service;

import test1.log.Log;
import top.hserver.core.ioc.annotation.Bean;

@Bean("t1")
public class TServiceImpl implements TService {
    @Log
    @Override
    public String t() {
        return "tttttttt";
    }
}
