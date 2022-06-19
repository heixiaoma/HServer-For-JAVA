package net.hserver.core.log;

import ch.qos.logback.classic.spi.LoggingEvent;
import ch.qos.logback.core.AppenderBase;
import net.hserver.core.interfaces.LogAdapter;
import net.hserver.core.ioc.IocUtil;
import java.util.List;

public class HServerLogAsyncAppender extends AppenderBase<LoggingEvent> {
    @Override
    protected void append(LoggingEvent eventObject) {
        List<LogAdapter> listBean = IocUtil.getListBean(LogAdapter.class);
        if (listBean != null&&listBean.size()>0) {
            for (LogAdapter logAdapter : listBean) {
                try {
                    logAdapter.log(eventObject);
                }catch (Throwable e){
                    e.printStackTrace();
                }
            }
        }
    }
}