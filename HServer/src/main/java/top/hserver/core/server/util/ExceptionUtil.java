package top.hserver.core.server.util;

import java.io.PrintWriter;
import java.io.StringWriter;

public class ExceptionUtil {

    private static Throwable getExceptionType(Throwable e) {
        return e;
    }

    private static String getExceptionMessage(Throwable e) {
        return e.getMessage();
    }

    private static String getExceptionSrintStackTrace(Throwable e) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        return sw.toString();
    }

    public static String getMessage(Throwable e) {
        StringBuilder stringBuilder = new StringBuilder();
//        stringBuffer.append("\nat " + ExceptionUtil.getExceptionType(e));
        stringBuilder.append("\n").append(ExceptionUtil.getExceptionSrintStackTrace(e));
        if (e.getMessage() != null) {
            stringBuilder.append("\nat ").append(ExceptionUtil.getExceptionMessage(e));
        }
        return stringBuilder.toString();
    }

    public static String getHtmlMessage(Throwable e) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("<br>").append(getExceptionSrintStackTrace(e).replaceAll("\n", "<br>").replaceAll("\t", "<div style='text-indent: 4em;display: inline-block;'>&nbsp;</div>"));
        if (e.getMessage() != null) {
            stringBuilder.append("<br><div style='text-indent: 4em;display: inline-block;'>&nbsp;</div>at ").append(getExceptionMessage(e));
        }
        return stringBuilder.toString();
    }

}
