package cn.hserver.mvc.template;

import java.util.Map;

public interface Template {
    String getTemplate(String template, Map map) throws Exception;
}
