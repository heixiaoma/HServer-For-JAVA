package com.test.task;

import com.hserver.core.interfaces.TaskJob;

public class TestTask implements TaskJob {

    @Override
    public void exec(Object... args) {
        String args_ = "";
        for (Object arg : args) {
            args_ += arg.toString();
        }
        System.out.println("测试定时器动态添加任务，参数是：" + args_);
    }
}
