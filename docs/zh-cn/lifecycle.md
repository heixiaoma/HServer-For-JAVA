
## **服务器启动完成执行的方法**

类必须要被@Bean注解，同时实现InitRunner接口，

```java
 @Bean
 public class RunInit implements InitRunner {
 
     @Autowired
     private User user;
 
     @Override
     public void init(String[] args) {
         System.out.println("初始化方法：注入的User对象的名字是-->"+user.getName());
     }
 }
```

## **服务器IOC重新初始化执行的方法**

类必须要被@Bean注解，同时实现ReInitRunner接口，

```java
 @Bean
 public class ReInit implements ReInitRunner {
 
     @Override
     public void reInit() {
         System.out.println("重新初始化之前，这个方法被执行，可以关闭一些线程或者或者叫资源，比如Redisson的相关内容");
     }
 }
```

## **服务器关闭回调执行的方法**

类必须要被@Bean注解，同时实现ServerCloseAdapter接口，

```java
@Bean
public class Close implements ServerCloseAdapter {
    @Override
    public void close() {
        System.out.println("服务开始关闭了，可以提前关闭资源或者有些没用处理完的，处理下");
        System.out.println("延时5秒关闭");
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("关闭了");
    }
}
```