添加依赖

```xml
<dependency>
    <groupId>cn.hserver</groupId>
    <artifactId>hserver-plugin-druid</artifactId>
</dependency>
```

设置数据源

```java
DruidDataSource ds=new DruidDataSource();
        ds.setUrl(mySqlUrl);
        ds.setUsername(mySqlUserName);
        ds.setPassword(mySqlPassword);
        ds.setDriverClassName(mySqlDriver);
//接入时需要把拦截器打开，不然不能监控
        ds.setFilters("stat,wall");
```

### 访问地址：

http://127.0.0.1:端口/druid/index.html
