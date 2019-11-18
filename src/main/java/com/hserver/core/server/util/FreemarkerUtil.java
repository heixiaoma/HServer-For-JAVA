package com.hserver.core.server.util;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateExceptionHandler;
import lombok.extern.slf4j.Slf4j;
import java.io.File;
import java.io.StringWriter;
import java.util.Map;

@Slf4j
public class FreemarkerUtil {

    private static final Configuration cfg = new Configuration(Configuration.VERSION_2_3_27);

    /**
     * 获取模板内容
     *
     * @param template 模板文件
     * @param map      模板参数
     * @return 渲染后的模板内容
     * @throws Exception       Exception
     */
    public static String getTemplate(String template, Map map) throws Exception {
        String templatePath = FreemarkerUtil.class.getResource("/template/").getPath();
//        log.info("FreemarkerUtil template  = " + template);
//        log.debug("templatePath = " + templatePath);
        cfg.setDirectoryForTemplateLoading(new File(templatePath));
        cfg.setDefaultEncoding("UTF-8");
        cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
        cfg.setLogTemplateExceptions(false);
        cfg.setWrapUncheckedExceptions(true);
        Template temp = cfg.getTemplate(template);
        StringWriter stringWriter = new StringWriter();
        temp.process(map, stringWriter);
        return stringWriter.toString();
    }
}
