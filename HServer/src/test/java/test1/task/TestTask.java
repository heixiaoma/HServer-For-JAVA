package test1.task;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.hserver.core.interfaces.TaskJob;

public class TestTask implements TaskJob {
    private static final Logger log = LoggerFactory.getLogger(TestTask.class);
    @Override
    public void exec(Object... args) {
        String args_ = "";
        for (Object arg : args) {
            args_ += arg.toString();
        }
        log.debug("测试定时器动态添加任务，参数是：{}",args_);
    }
}
