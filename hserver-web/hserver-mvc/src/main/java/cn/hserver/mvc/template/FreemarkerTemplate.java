package cn.hserver.mvc.template;

import freemarker.cache.ClassTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.TemplateExceptionHandler;

import java.io.StringWriter;
import java.util.Map;


/**
 * @author hxm
 */
public class FreemarkerTemplate implements Template{

    private static final Configuration CFG = new Configuration(Configuration.VERSION_2_3_27);

    static {
        CFG.setClassForTemplateLoading(FreemarkerTemplate.class, "/template");
        CFG.setTemplateLoader(new ClassTemplateLoader(FreemarkerTemplate.class, "/template"));
        CFG.setDefaultEncoding("UTF-8");
        CFG.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
        CFG.setLogTemplateExceptions(false);
        CFG.setWrapUncheckedExceptions(true);
    }

    /**
     * 获取模板内容
     *
     * @param template 模板文件
     * @param map      模板参数
     * @return 渲染后的模板内容
     * @throws Exception Exception
     */
    @Override
    public String getTemplate(String template, Map<?,?> map) throws Exception {
        freemarker.template.Template cfgTemplate = CFG.getTemplate(template);
        StringWriter stringWriter = new StringWriter();
        cfgTemplate.process(map, stringWriter);
        return stringWriter.toString();
    }

}
