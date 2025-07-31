package cn.hserver.core.logging;

import ch.qos.logback.classic.spi.LoggingEvent;
import ch.qos.logback.core.AppenderBase;

import java.util.List;

public class HServerLogAsyncAppender extends AppenderBase<LoggingEvent> {
    private static List<LogAdapter> listLogBean = null;

    public static void setHasLog(List<LogAdapter> listLogBean) {
        if (listLogBean != null && !listLogBean.isEmpty()) {
            HServerLogAsyncAppender.listLogBean = listLogBean;
        }
    }

    @Override
    protected void append(LoggingEvent eventObject) {
        if (listLogBean != null) {
            for (LogAdapter logAdapter : listLogBean) {
                try {
                    logAdapter.log(eventObject);
                } catch (Throwable e) {
                    System.err.println(e.getMessage());
                }
            }
        }
    }
}
