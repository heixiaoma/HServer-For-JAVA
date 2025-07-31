package cn.hserver.core.plugin.handler;

import cn.hserver.core.plugin.bean.PluginInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 插件适配器
 *
 * @author hxm
 */
public abstract class PluginAdapter {

    private static final Logger log = LoggerFactory.getLogger(PluginAdapter.class);



    /**
     * app 启动调用
     */
    public void startApp(){

   }

    /**
     * 开始初始化
     */
    public void ioc(){

   }

    /**
     * 启动完成
     */
    public void startedApp(){

   }

   public abstract PluginInfo getPluginInfo();


   public void printPluginInfo(){
       PluginInfo pluginInfo = getPluginInfo();
       if (pluginInfo != null) {
            String info = pluginInfo.info();
            if (info != null) {
                log.debug(info);
            }
        }
   }
}
