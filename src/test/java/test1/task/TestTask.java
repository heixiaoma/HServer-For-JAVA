package test1.task;

import lombok.extern.slf4j.Slf4j;
import top.hserver.core.interfaces.TaskJob;

@Slf4j
public class TestTask implements TaskJob {

    @Override
    public void exec(Object... args) {
        String args_ = "";
        for (Object arg : args) {
            args_ += arg.toString();
        }
        log.debug("测试定时器动态添加任务，参数是：{}",args_);
    }
}
