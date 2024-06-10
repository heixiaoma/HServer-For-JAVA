package cn.hserver.core.ioc.ref.init;

import cn.hserver.core.interfaces.*;
import cn.hserver.core.ioc.IocUtil;
import cn.hserver.core.ioc.annotation.Bean;
import cn.hserver.core.ioc.annotation.Task;
import cn.hserver.core.ioc.ref.PackageScanner;
import cn.hserver.core.plugs.PlugsManager;
import cn.hserver.core.task.TaskManager;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

public class InitBean extends Init{

    private final Set<Class<?>> listBeanClass=new HashSet<Class<?>>(){
        {
            //----------内部ListBean
            //检测这个Bean是否是初始化的类
            add(InitRunner.class);
            //检测这个Bean是否是Hum消息
            add(HumAdapter.class);
            //检测这个Bean是否是关闭服务的的类
            add(ServerCloseAdapter.class);
            //检测这个Bean是否是track的
            add(TrackAdapter.class);
            //检测这个Bean是否是日志扩展的数据
            add(LogAdapter.class);
            //单端口多协议情况
            add(ProtocolDispatcherAdapter.class);
            //连接后返回数据情况
            add(ProtocolDispatcherSuperAdapter.class);
        }
    };

    public InitBean(Set<String> packages) {
        super(packages);
    }

    @Override
    public void init(PackageScanner scan) throws Exception {
        Set<Class<?>> clasps = scan.getAnnotationList(Bean.class);
        //检查这个bean是否是插件的数据
        Set<Class<?>> classes = PlugsManager.getPlugin().iocInitBeanList();
        listBeanClass.addAll(classes);
        a:for (Class<?> aClass : clasps) {
            for (Class<?> beanClass : listBeanClass) {
                if (beanClass.isAssignableFrom(aClass)) {
                    IocUtil.addListBean(beanClass.getName(), aClass.newInstance());
                    continue a;
                }
            }
            //检查注解里面是否有值
            Bean annotation = aClass.getAnnotation(Bean.class);
            if (!annotation.value().trim().isEmpty()) {
                IocUtil.addBean(annotation.value(), aClass.newInstance());
            } else {
                IocUtil.addBean(aClass.getName(), aClass.newInstance());
            }

            //检测下Bean里面是否带又Task任务洛，带了就给他安排了
            Method[] methods = aClass.getDeclaredMethods();

            for (Method method : methods) {
                Task task = method.getAnnotation(Task.class);
                if (task == null) {
                    continue;
                }
                if (!annotation.value().trim().isEmpty()) {
                    TaskManager.initTask(task.name(), task.time(), annotation.value(), method);
                } else {
                    TaskManager.initTask(task.name(), task.time(), aClass.getName(), method);
                }
            }

        }
    }
}
