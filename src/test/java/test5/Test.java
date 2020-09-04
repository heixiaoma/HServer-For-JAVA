package test5;

import test1.bean.User;
import top.hserver.cloud.util.SerializationUtil;
import top.hserver.core.queue.QueueSerialization;

import java.io.IOException;

public class Test {

    public static void main(String[] args) throws Exception {
        QueueSerialization queueSerialization = new QueueSerialization();
//        User user = new User();
//        user.setName("jkk");
//        user.setSex("男");
//        long l = System.currentTimeMillis();
//        int j=10;
//        for (int i = 0; i <j ; i++) {
//            user.setAge(i);
//            byte[] serialize = SerializationUtil.serialize(user);
//            queueSerialization.cacheQueue(serialize);
//        }
//        System.out.println(System.currentTimeMillis()-l);
    }

}
