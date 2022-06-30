
## **单元测试**

1. web 服务测试

```java
/**
 * @author hxm
 */
@RunWith(HServerTestServer.class)
public class TestWebApp {

    @Test
    public void start(){
    }

}
```

2. 非web服务测试

```java

@RunWith(HServerTest.class)
public class test2 {

    @Autowired
    private TestBean testBean;

    @Autowired
    private Tom tom;

    @Test
    public void test(){
        System.out.println(testBean.hello());
    }

    @Test
    public void test2(){
        System.out.println(tom.toString());
    }

}
```
