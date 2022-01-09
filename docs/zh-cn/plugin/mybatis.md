## mybatis使用

```text
原生 mybatis 
<dependency>
    <groupId>net.hserver.mybatis.plugin</groupId>
    <artifactId>hserver-mybatis-plugin</artifactId>
    <version>1.0</version>
</dependency>

从2.0开始 插件集成 mybatis-plus
<dependency>
    <groupId>net.hserver.mybatis.plugin</groupId>
    <artifactId>hserver-mybatis-plugin</artifactId>
    <version>2.1</version>
</dependency>
```

### 添加插件依赖
```xml
<dependency>
    <groupId>net.hserver.mybatis.plugin</groupId>
    <artifactId>hserver-mybatis-plugin</artifactId>
    <version>2.1</version>
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
public class MybatisConfig {
    @Bean
    public net.hserver.mybatis.plugin.bean.MybatisConfig mybatisConfig() {
        net.hserver.mybatis.plugin.bean.MybatisConfig mybatisConfig = new net.hserver.mybatis.plugin.bean.MybatisConfig();
        HikariDataSource ds = new HikariDataSource();
        ds.setJdbcUrl("jdbc:mysql://127.0.0.1:3306/mydb?useUnicode=true&characterEncoding=UTF-8&useSSL=false&serverTimezone=UTC");
        ds.setUsername("root");
        ds.setPassword("haosql");
        ds.setDriverClassName("com.mysql.cj.jdbc.Driver");
        //默认数据源
        mybatisConfig.addDataSource(ds);
        //多数据源
        mybatisConfig.addDataSource("数据源1",ds);
        //resource/mapper 全部.xml扫描
        mybatisConfig.setMapperLocations("mapper");
        return mybatisConfig;
    }

}
```

### Mapper
```java
  //默认数据源
    @Mybatis
    public interface UserDao  {
    
        @Update("update user set age = #{age} where id = #{id}")
        void update(@Param("id") Integer id, @Param("age")Integer age);
    
        //映射xml
        List<User> select();
    }
    
    
    /**
     * Mybatis Plus支持BaseMapper 具体参考 官方文档 
     */
    @Mybatis
    public interface UserMapper extends BaseMapper<User> {
    
        @Select("select * from user")
        List<Map> getAll();
    
        @Update("UPDATE `mydb`.`user` SET `age` =#{age} WHERE `id` = #{id}")
        void update1(@Param("id") Integer id, @Param("age")Integer age);
    }


    //多数据源情况下指定一个数据源名字
    @Mybatis("数据源1")
    public interface UserDao  {
    
        @Update("update user set age = #{age} where id = #{id}")
        void update(@Param("id") Integer id, @Param("age")Integer age);
    
        //映射xml
        List<User> select();
    }
    

```

### 事务
```java
    @Bean
    public class UserService {
        @Autowired
        private UserDao userDao;
     
        @Tx(rollbackFor = ArithmeticException.class,timeoutMillisecond = 1000)
            public boolean update() {
                System.out.println("我的："+Thread.currentThread().getName());
                userDao.update(2222, 1);
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

### SqlSessionFactory
```java
    //如果有需求，需要使用SqlSessionFactory你可以直接注入进来。
    //默认数据源注入
    @Autowired
    private SqlSessionFactory sqlSessionFactory1;

    //多数据源情况指定数据源名字
    @Autowired("数据源1")
    private SqlSessionFactory sqlSessionFactory2;
```