## 注解认识

根据上面的例子，大家应该理解是非常容易的，和springboot很相似。接下我们了解下注解这里，注解只是做简单描述，具体使用在后面的章节会演示出来

|           注解           |                           描述信息                           |
| :----------------------: | :----------------------------------------------------------: |
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
|       @Controller        |  标记类为控制器 @Controller 参数可以指定一个URL 和 一个名字  |
|   @RequiresPermissions   |                           权限注解                           |
|      @RequiresRoles      |                           角色注解                           |
|          @Sign           | 作用在控制器方法上.可以根据他来实现sign检查当然你可以用拦截器自己处理 |
|          @Task           |                    定时器使用，具体看例子                    |
|        @WebSocket        |               websocket注解，具体看下面的介绍                |
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

