## mybatis mybatis-flex

### 文档地址 https://mybatis-flex.com/

```text
 
<dependency>
    <groupId>cn.hserver</groupId>
    <artifactId>hserver-plugin-mybatis_flex</artifactId>
</dependency>
```

### 添加插件依赖

```xml
 <dependency>
    <artifactId>hserver-plugin-mybatis_flex</artifactId>
    <groupId>cn.hserver</groupId>
</dependency>
<dependency>
    <groupId>mysql</groupId>
    <artifactId>mysql-connector-java</artifactId>
    <version>8.0.18</version>
</dependency>
<dependency>
    <groupId>com.zaxxer</groupId>
    <artifactId>HikariCP</artifactId>
    <version>2.7.2</version>
</dependency>
```


### 添加配置类

```java

@Configuration
public class DbConfig {
    @Value("mysql.url")
    private String mySqlUrl;

    @Value("mysql.userName")
    private String mySqlUserName;

    @Value("mysql.password")
    private String mySqlPassword;

    @Value("mysql.driver")
    private String mySqlDriver;

    @Bean
    public MybatisConfig sql() throws SQLException {
        HikariDataSource ds = new HikariDataSource();
        ds.setJdbcUrl(mySqlUrl);
        ds.setUsername(mySqlUserName);
        ds.setPassword(mySqlPassword);
        ds.setDriverClassName(mySqlDriver);
        ds.setMaximumPoolSize(10);
        ds.setConnectionTimeout(3000); // 连接超时时间（毫秒）
        ds.setIdleTimeout(600000); // 空闲连接超时时间（毫秒）
        ds.setMaxLifetime(1800000); // 连接最大生命周期（毫秒）

        MybatisConfig mybatisConfig = new MybatisConfig();
        mybatisConfig.setMapUnderscoreToCamelCase(true);
        //默认数据源,或者k,v 添加数据源
        mybatisConfig.addDataSource(ds);
        //resource/mapper 全部.xml扫描
        mybatisConfig.setMapperLocations("mapper");
        //分页插件
        return mybatisConfig;
    }

}

```

### Mapper
```java
  
    /**
     * Mybatis Flex支持BaseMapper 具体参考 官方文档 
     */
    @Mybatis
    public interface UserMapper extends BaseMapper<User> {
    
        @Select("select * from user")
        List<Map> getAll();
    
        @Update("UPDATE `mydb`.`user` SET `age` =#{age} WHERE `id` = #{id}")
        void update1(@Param("id") Integer id, @Param("age")Integer age);
    }
```

### 事务
```java
    @Bean
    public class UserService {
        @Autowired
        private UserDao userDao;

        public boolean update() {
            userDao.update(2222, 1);
            return true;
        }
        
        public boolean updateTx() {
            return Db.tx(() -> {
                System.out.println("我的："+Thread.currentThread().getName());
                userDao.update(2222, 1);
                //        System.out.println(1 / 0);
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return true;
            });
        }
     }
     
```

### 多数据源切换
```java
try{
    DataSourceKey.use("ds2");
    List<Row> rows = Db.selectAll("tb_account");
    System.out.println(rows);
}finally{
    DataSourceKey.clear();
}
```

### 动态表名
```java
// 业务处理时在动态切换表明
try{
    TableManager.setHintTableMapping("tb_account", "tb_account_01");

    //这里写您的业务逻辑

} finally {
    TableManager.clear();
}

//全局动态表名，可以设置在主函数开始
TableManager.setDynamicTableProcessor(tableName -> {
    String yearAndMonth = TableDateUtil.getTableDate();
    return yearAndMonth + "_" + tableName;
});

```