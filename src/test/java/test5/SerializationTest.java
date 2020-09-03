package test5;

import test1.bean.User;
import top.hserver.cloud.util.SerializationUtil;


public class SerializationTest {

    public static void main(String[] args) {

        User user = new User();
        user.setAge(100);
        user.setName("jkk");
        user.setSex("男");
        User deserialize = null;

        long l = System.currentTimeMillis();
        int j=10000000;
        for (int i = 0; i <j ; i++) {
            byte[] serialize = SerializationUtil.serialize(user);
            deserialize = SerializationUtil.deserialize(serialize, User.class);
        }
        System.out.println(j+"个对象，耗时：" + (System.currentTimeMillis() - l) + "ms");
        System.out.println(user.hashCode());
        System.out.println(deserialize.hashCode());
        System.out.println("------------");
        System.out.println(user);
        System.out.println(deserialize);
    }
}
