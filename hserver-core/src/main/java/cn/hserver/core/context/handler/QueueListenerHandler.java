package cn.hserver.core.context.handler;

import cn.hserver.core.ioc.bean.BeanDefinition;
import cn.hserver.core.queue.QueueEventHandler;
import cn.hserver.core.queue.QueueManager;
import cn.hserver.core.queue.annotation.QueueHandler;
import cn.hserver.core.queue.annotation.QueueListener;
import cn.hserver.core.queue.bean.QueueHandleInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.Map;


public class QueueListenerHandler implements AnnotationHandler{
    private static final Logger log = LoggerFactory.getLogger(QueueListenerHandler.class);

    @Override
    public void handle(Class<?> clazz, Map<String, BeanDefinition> beanDefinitions) {
        String className = clazz.getName();
        if (clazz.isAnnotationPresent(QueueListener.class)) {
            // 注册配置类本身作为Bean
            BeanDefinition configBeanDef = new BeanDefinition();
            configBeanDef.setBeanClass(clazz);
            QueueListener queueListener = clazz.getAnnotation(QueueListener.class);
            for (Method declaredMethod : clazz.getDeclaredMethods()) {
                if (declaredMethod.isAnnotationPresent(QueueHandler.class)) {
                    QueueHandleInfo eventHandleInfo = new QueueHandleInfo(queueListener.queueName());
                    eventHandleInfo.setThreadSize(queueListener.threadSize());
                    eventHandleInfo.setQueueEventHandler(new QueueEventHandler(configBeanDef.getDefaultBeanName(), declaredMethod));
                    QueueManager.addQueueListener(eventHandleInfo);
                    beanDefinitions.put(configBeanDef.getDefaultBeanName(), configBeanDef);
                    log.debug("寻找队列 [{}] 的方法 [{}.{}]", queueListener.queueName(), clazz.getSimpleName(),
                            declaredMethod.getName());
                    return;
                }
            }
        }
    }
}
