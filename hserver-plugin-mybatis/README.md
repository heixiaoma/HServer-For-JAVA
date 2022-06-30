# HServer SQL Mybatis 插件


```text

<dependency>
   <groupId>cn.hserver</groupId>
   <artifactId>hserver-plugin-mybatis</artifactId>
</dependency>
```

```java

@Configuration
public class MybatisConfig {
    @Bean
    public cn.hserver.plugin.mybatis.bean.MybatisConfig mybatisConfig() {
        cn.hserver.plugin.mybatis.bean.MybatisConfig mybatisConfig = new cn.hserver.plugin.mybatis.bean.MybatisConfig();
        HikariDataSource ds = new HikariDataSource();
        ds.setJdbcUrl("jdbc:mysql://127.0.0.1:3306/mydb?useUnicode=true&characterEncoding=UTF-8&useSSL=false&serverTimezone=UTC");
        ds.setUsername("root");
        ds.setPassword("haosql");
        ds.setDriverClassName("com.mysql.cj.jdbc.Driver");
        //默认数据源
        mybatisConfig.addDataSource(ds);
        //多数据源
        mybatisConfig.addDataSource("数据源1", ds);
        //resource/mapper 全部.xml扫描
        mybatisConfig.setMapperLocations("mapper");
        return mybatisConfig;
    }

}

```


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
        
        
```

```java
    //多数据源情况下指定一个数据源名字
    @Mybatis("数据源1")
    public interface UserDao  {
    
        @Update("update user set age = #{age} where id = #{id}")
        void update(@Param("id") Integer id, @Param("age")Integer age);
    
        //映射xml
        List<User> select();
    }
```

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

```java
    
    //如果有需求，需要使用SqlSessionFactory你可以直接注入进来。

    //默认数据源注入
    @Autowired
    private SqlSessionFactory sqlSessionFactory1;

    //多数据源情况指定数据源名字
    @Autowired("数据源1")
    private SqlSessionFactory sqlSessionFactory2;


```