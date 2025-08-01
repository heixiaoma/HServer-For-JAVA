package cn.hserver.mvc;

import cn.hserver.core.plugin.bean.PluginInfo;
import cn.hserver.core.plugin.handler.PluginAdapter;
import cn.hserver.mvc.annotation.Controller;

public class MvcPlugin extends PluginAdapter {

    @Override
    public PluginInfo getPluginInfo() {
        return new PluginInfo.Builder().name("MVC").description( "灵活高性能的WEB框架").build();
    }

    @Override
    public void iocStartScan(Class<?> clazz) {
        if(clazz.isAnnotationPresent(Controller.class)){
            Controller annotation = clazz.getAnnotation(Controller.class);
        }
    }
}
