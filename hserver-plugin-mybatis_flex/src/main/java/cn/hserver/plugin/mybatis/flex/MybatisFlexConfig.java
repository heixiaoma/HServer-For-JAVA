package cn.hserver.plugin.mybatis.flex;

import cn.hserver.core.ioc.IocUtil;
import cn.hserver.plugin.mybatis.flex.bean.MybatisConfig;
import com.mybatisflex.core.MybatisFlexBootstrap;

import java.util.Set;

public class MybatisFlexConfig {

    public static MybatisFlexBootstrap init(Set<Class<?>> mappers){
        MybatisConfig mybatisConfig = IocUtil.getBean(MybatisConfig.class);
        MybatisFlexBootstrap instance = MybatisFlexBootstrap.getInstance();
        //配置数据源
        mybatisConfig.getDataSources().forEach(instance::addDataSource);
        //对mapper进行包装
        mappers.forEach(instance::addMapper);
       return instance.start();
    }

}
