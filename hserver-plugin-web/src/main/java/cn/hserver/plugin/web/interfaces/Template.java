package cn.hserver.plugin.web.interfaces;

import java.util.Map;

public interface Template {
    String getTemplate(String template, Map map) throws Exception;
}
