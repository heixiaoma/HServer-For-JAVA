package top.hserver.core.server.util;

import freemarker.cache.ClassTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateExceptionHandler;
import lombok.extern.slf4j.Slf4j;
import java.io.StringWriter;
import java.util.Map;


@Slf4j
public class FreemarkerUtil {

    private static final Configuration cfg = new Configuration(Configuration.VERSION_2_3_27);

    static {
        cfg.setClassForTemplateLoading(FreemarkerUtil.class, "/template");
        cfg.setTemplateLoader(new ClassTemplateLoader(FreemarkerUtil.class, "/template"));
        cfg.setDefaultEncoding("UTF-8");
        cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
        cfg.setLogTemplateExceptions(false);
        cfg.setWrapUncheckedExceptions(true);
    }

    /**
     * 获取模板内容
     *
     * @param template 模板文件
     * @param map      模板参数
     * @return 渲染后的模板内容
     * @throws Exception Exception
     */
    public static String getTemplate(String template, Map map) throws Exception {


        Template temp = cfg.getTemplate(template);
        StringWriter stringWriter = new StringWriter();
        temp.process(map, stringWriter);
        return stringWriter.toString();
    }

}
