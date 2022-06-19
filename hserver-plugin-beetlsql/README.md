# HServer SQL插件

```
版本查询 https://repo1.maven.org/maven2/net/hserver/plugins/beetlsql/plugins.beetlsql/

当前 3.3

beetlsql 3.4.3-RELEASE

```

```
<dependency>
  <groupId>cn.hserver.plugins.beetlsql</groupId>
  <artifactId>plugins.beetlsql</artifactId>
  <version>3.3</version>
</dependency>

```

```
    @BeetlSQL
    public interface UserDao2 extends BaseMapper<User> {
    
        @Update
        @Sql("update user set age = ? where id = ?")
        void update(Integer age,int id);
    
        List<User> select();
    }
```

    @Bean
    public class UserService {
        @Autowired
        private UserDao2 userDao2;
     
        @Tx(rollbackFor = ArithmeticException.class,timeoutMillisecond = 1000)
            public boolean update() {
                System.out.println("我的："+Thread.currentThread().getName());
                userDao2.update(2222, 1);
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