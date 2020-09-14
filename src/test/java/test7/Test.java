package test7;

import test1.bean.User;
import top.hserver.core.server.context.ConstConfig;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Test {
    public static void main(String[] args) {
        Map<String,Object> map=new HashMap<>();
        map.put("name","小明");
        map.put("age","20");
        List<String> test=new ArrayList<>();
        test.add("dsds");
        test.add("d123");
        test.add("dsdsdsad");
        map.put("test",test);

        User user = ConstConfig.JSON.convertValue(map, User.class);
        System.out.println(user);
    }
}
