package com.hserver.core.server.util;

import java.io.PrintWriter;
import java.io.StringWriter;

public class ExceptionUtil {

    private static Throwable getExceptionType(Exception e) {
        return e;
    }

    private static String getExceptionMessage(Exception e) {
        return e.getMessage();
    }

    private static String getExceptionSrintStackTrace(Exception e) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        return sw.toString();
    }

    public static String getMessage(Exception e) {
        StringBuffer stringBuffer = new StringBuffer();
//        stringBuffer.append("\nat " + ExceptionUtil.getExceptionType(e));
        stringBuffer.append("\n" + ExceptionUtil.getExceptionSrintStackTrace(e));
        if (e.getMessage() != null) {
            stringBuffer.append("\nat " + ExceptionUtil.getExceptionMessage(e));
        }
        return stringBuffer.toString();
    }

}
