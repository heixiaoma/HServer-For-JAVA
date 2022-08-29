## 注解认识

根据上面的例子，大家应该理解是非常容易的，和springboot很相似。接下我们了解下注解这里，注解只是做简单描述，具体使用在后面的章节会演示出来

|           注解           |                           描述信息                           |
| :----------------------: | :----------------------------------------------------------: |
|          @Bean           | 将当前类加入IOC中，类似spring的@Component注解，可以名字放入ioc 如@Bean("Test") |
|        @Autowired        | 将ioc里的某个对象注入给某个字段，和spring用法类似,可以名字注入ioc 如@Autowired("Test") |
|      @Order      | 排序注解 值越小，优先级越高 (LimitAdapter.class, FilterAdapter.class, GlobalException.class, InitRunner.class, ResponseAdapter.class, ProtocolDispatcherAdapter.class, ServerCloseAdapter.class) 这些子类支持排序 |
|      @Configuration      | 配置注解，这个和springboot有相似之处（这个类中可以注入 @NacosClass,@NacosValue,@Value,@ConfigurationProperties这些注解产生的对象） |
| @ConfigurationProperties |     配置类，和springboot相似 将Properties转为对象放入IOC     |
|          @Hook           |                         AOP操作使用                          |
|          @Track          | 链路跟踪注解，如果你想检查某些方法的耗时或者其他监控，可以用这个注解，具体看下面的介绍 |
|          @Value          |             用来把Properties字段注入到类的字段里             |
|            @QueueListener            |                            标记一个类为队列处理类                            |
|            @QueueHandler            |                           标记一个方法为队列处理方法                           |

