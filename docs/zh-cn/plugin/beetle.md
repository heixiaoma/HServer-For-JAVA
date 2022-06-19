## beetlsql使用

### 添加插件依赖

```xml

<dependency>
    <groupId>net.hserver.plugin.beetlsql</groupId>
    <artifactId>plugins.beetlsql</artifactId>
    <version>3.3</version>
</dependency>

        <!--添加连接池和MySQL驱动-->
<dependency>
<groupId>com.zaxxer</groupId>
<artifactId>HikariCP</artifactId>
<version>2.7.2</version>
</dependency>

<dependency>
<groupId>mysql</groupId>
<artifactId>mysql-connector-java</artifactId>
<version>8.0.18</version>
</dependency>

```

### 添加配置类
```java
@Configuration
public class DbConfig {

    @Value("mysql.url")
    private String mySqlUrl;

    @Value("mysql.username")
    private String mySqlUsername;

    @Value("mysql.password")
    private String mySqlPassword;

    @Value("mysql.driver")
    private String mySqlDriver;

    //默认数据源
    @Bean
    public SQLManager sql() {
        HikariDataSource ds = new HikariDataSource();
        ds.setJdbcUrl(mySqlUrl);
        ds.setUsername(mySqlUsername);
        ds.setPassword(mySqlPassword);
        ds.setDriverClassName(mySqlDriver);
        ConnectionSource source = ConnectionSourceHelper.getSingle(ds);
        SQLManagerBuilder builder = new SQLManagerBuilder(source);
        builder.setSqlLoader(new MarkdownClasspathLoader());
        builder.setNc(new UnderlinedNameConversion());
        builder.setInters(new Interceptor[]{new DebugInterceptor()});
        return builder.build();
    }

    //数据源1
    @Bean("data_1")
    public SQLManager sql1() {
        HikariDataSource ds = new HikariDataSource();
        ds.setJdbcUrl(mySqlUrl);
        ds.setUsername(mySqlUsername);
        ds.setPassword(mySqlPassword);
        ds.setDriverClassName(mySqlDriver);
        ConnectionSource source = ConnectionSourceHelper.getSingle(ds);
        SQLManagerBuilder builder = new SQLManagerBuilder(source);
        builder.setSqlLoader(new MarkdownClasspathLoader());
        builder.setNc(new UnderlinedNameConversion());
        builder.setInters(new Interceptor[]{new DebugInterceptor()});
        return builder.build();
    }
    
}
```


### 使用Beetlsql

```java
package com.system.domain.entity;
import lombok.Data;
import org.beetl.sql.annotation.entity.Table;
import java.util.Date;

/**
 * @author hxm
 */
@Data
@Table(name = "sys_user")
public class UserEntity {

    private Integer id;
    private String username;
    private String password;
    private String nickName;
    private String avatar;
    private String state;
    private Date createTime;
    private Date updateTime;

}


```



### Mapper接口
```java
    @BeetlSQL
    public interface UserDao2 extends BaseMapper<UserEntity> {
    
        @Update
        @Sql("update user set nickName = ? where id = ?")
        void update(String nickName,int id);
    
        List<UserEntity> select();
    }


    /**
     * 使用的数据源1
     */
    @BeetlSQL("data_1")
    public interface UserDao2 extends BaseMapper<UserEntity> {

        @Update
        @Sql("update user set nickName = ? where id = ?")
        void update(String nickName,int id);

        List<UserEntity> select();
    }

```

### 事务操作
```java

@Bean
public class UserService {
    @Autowired
    private UserDao2 userDao2;
 
    @Tx(rollbackFor = ArithmeticException.class,timeoutMillisecond = 1000)
        public boolean update() {
            System.out.println("我的："+Thread.currentThread().getName());
            userDao2.update("Test", 1);
    //        System.out.println(1 / 0);
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return false;
        }
 }

```


beetlsql默认支持md的文件,默认路径在resources/sql路径下，类似Mybatis的xml文件，举栗子 UserDao2的select方法
- resources/sql/sys_user.md
```text
selectRole
===
    SELECT * FROM sys_user
```


更具体的使用方法可以参考Beetlsql官方网的教程，比如包括一些查询什么的，拉米大的单表操作之类的

### 测试Demo 多数据源
```text
https://gitee.com/HServer/hserver-for-java-beetlsql/tree/master
```