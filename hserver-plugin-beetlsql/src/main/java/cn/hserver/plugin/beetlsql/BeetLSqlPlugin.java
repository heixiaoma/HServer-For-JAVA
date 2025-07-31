package cn.hserver.plugin.beetlsql;

import cn.hserver.core.context.handler.AnnotationHandler;
import cn.hserver.core.plugin.bean.PluginInfo;
import cn.hserver.core.plugin.handler.PluginAdapter;
import cn.hserver.plugin.beetlsql.handler.BeetlSQLHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



/**
 * @author hxm
 */
public class BeetLSqlPlugin extends PluginAdapter {

    private static final Logger log = LoggerFactory.getLogger(BeetLSqlPlugin.class);

    @Override
    public void ioc(){
        AnnotationHandler.addHandler(new BeetlSQLHandler());
    }

    @Override
    public PluginInfo getPluginInfo() {
        return new PluginInfo.Builder()
                .name("beetlsql插件")
                .description("简洁方便，功能强大的ORM工具，从2015年开始研发")
                .build();
    }
}
