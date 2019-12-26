package org.slf4j.impl;

import java.util.HashMap;
import java.util.Map;

public interface Constant {

    String DATE_TIME_FORMAT_STR_DEFAULT = "yyyy/MM/dd HH:mm:ss";

    String CONFIGURATION_FILE = "application";
    String CONFIGURATION_FILE0 = "app";

    String LOG_ERR = "System.err";
    String LOG_OUT = "System.out";

    String LOG_KEY_PREFIX = "log.";

    /**
     * 缓存输出流
     */
    String CACHE_OUTPUT_STREAM_STRING_KEY = "log.cacheOutputStream";

    /**
     * 支架中的水平
     */
    String LEVEL_IN_BRACKETS_KEY = "log.levelInBrackets";

    /**
     * 日志名字
     */
    String LOG_NAME_KEY = "log.name";

    /**
     * app名字
     */
    String APP_NAME_KEY = "app.name";

    /**
     * 目录
     */
    String LOG_DIR_KEY = "log.dir";

    /**
     * 最大尺寸
     */
    String MAX_SIZE_KEY = "log.maxSize";

    /**
     * 显示短名称
     */
    String SHOW_SHORT_NAME_KEY = "log.shortName";

    /**
     * 显示日志名称
     */
    String SHOW_LOG_NAME_KEY = "log.showLogName";

    /**
     * 显示线程名称
     */
    String SHOW_THREAD_NAME_KEY = "log.showThread";

    /**
     * 日期模式
     */
    String DATE_TIME_FORMAT_KEY = "log.datePattern";

    /**
     * 显示日期
     */
    String SHOW_DATE_TIME_KEY = "log.showDate";

    /**
     * 控制台是否显示
     */
    String SHOW_CONSOLE_KEY = "log.console";

    /**
     * 日志级别
     */
    String ROOT_LEVEL_KEY = "log.rootLevel";

    /**
     * 禁用颜色
     */
    String DISABLE_COLOR = "log.disableColor";

    Map<Integer, String> LOG_DESC_MAP = new HashMap<Integer, String>() {
        private static final long serialVersionUID = -8216579733086302246L;

        {
            put(0, Ansi.White.and(Ansi.Bold).format("TRACE"));
            put(10, Ansi.Cyan.and(Ansi.Bold).format("DEBUG"));
            put(20, Ansi.Green.and(Ansi.Bold).format(" INFO"));
            put(30, Ansi.Yellow.and(Ansi.Bold).format(" WARN"));
            put(40, Ansi.Red.and(Ansi.Bold).format("ERROR"));

            put(50, "TRACE");
            put(60, "DEBUG");
            put(70, " INFO");
            put(80, " WARN");
            put(90, "ERROR");
        }
    };

    String TRACE = "trace";
    String INFO = "info";
    String DEBUG = "debug";
    String WARN = "warn";
    String ERROR = "error";
    String OFF = "error";
}