package test;

import org.junit.Test;
import org.junit.runner.RunWith;
import top.hserver.core.ioc.annotation.Autowired;
import top.hserver.core.ioc.annotation.HServerBootTest;
import top.hserver.core.test.HServerTest;
import top.test.TestWebApp;
import top.test.bean.User;

@RunWith(HServerTest.class)
@HServerBootTest(TestWebApp.class)
public class test {

    @Autowired
    private User user;

    @Test
    public void main(){
        System.out.println(user.getName());;
    }
}
