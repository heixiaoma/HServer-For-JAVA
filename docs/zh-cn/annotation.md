## 注解认识

根据上面的例子，大家应该理解是非常容易的，和springboot很相似。接下我们了解下注解这里，注解只是做简单描述，具体使用在后面的章节会演示出来

|           注解           |                           描述信息                           |
| :----------------------: | :----------------------------------------------------------: |
|          @Bean           | 将当前类加入IOC中，类似spring的@Component注解，可以名字放入ioc 如@Bean("Test") |
|        @Autowired        | 将ioc里的某个对象注入给某个字段，和spring用法类似,可以名字注入ioc 如@Autowired("Test") |
|     @RequestMapping      |                 这个和springmvc的注解很相似                  |
|           @GET           |                 请求类型注解类似@GetMapping                  |
|          @POST           |                         请求类型注解                         |
|           @PUT           |                         请求类型注解                         |
|          @HEAD           |                         请求类型注解                         |
|          @PATCH          |                         请求类型注解                         |
|         @DELETE          |                         请求类型注解                         |
|         @OPTIONS         |                         请求类型注解                         |
|         @CONNECT         |                         请求类型注解                         |
|          @TRACE          |                         请求类型注解                         |
|      @Order      | 排序注解 值越小，优先级越高 (LimitAdapter.class, FilterAdapter.class, GlobalException.class, InitRunner.class, ReInitRunner.class, ResponseAdapter.class, ProtocolDispatcherAdapter.class, RpcAdapter.class, ServerCloseAdapter.class) 这些子类支持排序 |
|      @Configuration      | 配置注解，这个和springboot有相似之处（这个类中可以注入 @NacosClass,@NacosValue,@Value,@ConfigurationProperties这些注解产生的对象） |
| @ConfigurationProperties |     配置类，和springboot相似 将Properties转为对象放入IOC     |
|       @Controller        |  标记类为控制器 @Controller 参数可以指定一个URL 和 一个名字  |
|          @Hook           |                         AOP操作使用                          |
|   @RequiresPermissions   |                           权限注解                           |
|      @RequiresRoles      |                           角色注解                           |
|        @Resource         |               RPC对象的注入使用.可以按名字注入               |
|       @RpcService        |         标记一个Service是一个RPC服务，可以给一个名字         |
|          @Sign           | 作用在控制器方法上.可以根据他来实现sign检查当然你可以用拦截器自己处理 |
|          @Task           |                    定时器使用，具体看例子                    |
|          @Track          | 链路跟踪注解，如果你想检查某些方法的耗时或者其他监控，可以用这个注解，具体看下面的介绍 |
|          @Value          |             用来把Properties字段注入到类的字段里             |
|        @WebSocket        |               websocket注解，具体看下面的介绍                |
|            @QueueListener            |                            标记一个类为队列处理类                            |
|            @QueueHandler            |                           标记一个方法为队列处理方法                           |
| @AssertFalse | 字段为必须为false  |
| @AssertTrue |字段为必须为true|
| @Length |字段CharSequence 类型的长度必须是 length 长|
| @Max |字段值必须大于这个值，number|
| @Min |字段值必须小于这个值，number|
| @NotBlank |字段不能为null同时不是 ""|
| @NotEmpty |CharSequence 集合 map 数组 不是null 长度或者size 大于0|
| @NotNull |字段不能为Null|
| @Null |字段必须为Null|
| @Pattern |字段CharSequence 必须满足这个正则|
| @Size |字段 CharSequence 集合 map 数组必须在这范围内|
| @ApiImplicitParams |API生成标记|
| @ApiImplicitParam |API生成标记|

