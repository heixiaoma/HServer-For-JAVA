package net.hserver.core.server.util;

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
        return "\n" + ExceptionUtil.getExceptionSrintStackTrace(e);
    }

    public static String getHtmlMessage(Throwable e) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("<br>").append(getExceptionSrintStackTrace(e).replaceAll("\n", "<br>").replaceAll("\t", "<div style='text-indent: 4em;display: inline-block;'>&nbsp;</div>"));
        if (e.getMessage() != null) {
            stringBuilder.append("<br><div style='text-indent: 4em;display: inline-block;'>&nbsp;</div>");
        }
        return stringBuilder.toString();
    }

}
