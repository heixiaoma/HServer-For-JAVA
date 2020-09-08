package test5;

import org.junit.runner.RunWith;
import top.hserver.cloud.util.SerializationUtil;
import top.hserver.core.queue.QueueSerialization;
import top.hserver.core.queue.User;

import java.io.IOException;

public class Test {

    public static void main(String[] args) throws Exception {

        QueueSerialization queueSerialization = new QueueSerialization();
        for (int i = 0; i < 20000; i++) {
            top.hserver.core.queue.User user = new top.hserver.core.queue.User();
            user.setName("小王" + i);
            user.setAge(i);
            queueSerialization.cacheQueue(SerializationUtil.serialize(user));
        }
        for (int i = 0; i < 1000; i++) {
            byte[] bytes = queueSerialization.fetchQueue();
            User deserialize = SerializationUtil.deserialize(bytes, User.class);
            System.out.println(deserialize.getName());
        }
        for (int i = 0; i < 20000; i++) {
            top.hserver.core.queue.User user = new top.hserver.core.queue.User();
            user.setName("小王" + i);
            user.setAge(i);
            queueSerialization.cacheQueue(SerializationUtil.serialize(user));
        }

        try{
            for (int i = 0; i < 100000; i++) {
                byte[] bytes = queueSerialization.fetchQueue();
                User deserialize = SerializationUtil.deserialize(bytes, User.class);
                System.out.println(deserialize.getName());
            }
        }catch (Exception e){
        }

        System.out.println("------------------");

        for (int i = 0; i < 20000; i++) {
            top.hserver.core.queue.User user = new top.hserver.core.queue.User();
            user.setName("小王" + i);
            user.setAge(i);
            queueSerialization.cacheQueue(SerializationUtil.serialize(user));
        }
        for (int i = 0; i < 1000; i++) {
            byte[] bytes = queueSerialization.fetchQueue();
            User deserialize = SerializationUtil.deserialize(bytes, User.class);
            System.out.println(deserialize.getName());
        }

        try{
            for (int i = 0; i < 100000; i++) {
                byte[] bytes = queueSerialization.fetchQueue();
                User deserialize = SerializationUtil.deserialize(bytes, User.class);
                System.out.println(deserialize.getName());
            }
        }catch (Exception e){}


//        性能测试

        System.out.println("-------- 性能测试----------");
        int j=100000;
        long l = System.currentTimeMillis();
        for (int i = 0; i < j; i++) {
            top.hserver.core.queue.User user = new top.hserver.core.queue.User();
            user.setName("小王" + i);
            user.setAge(i);
            queueSerialization.cacheQueue(SerializationUtil.serialize(user));
        }

        System.out.println(j+"个对象序列化耗时："+(System.currentTimeMillis()-l)/1000.0+"s");

        long l1 = System.currentTimeMillis();
        for (int i = 0; i < j; i++) {
            byte[] bytes = queueSerialization.fetchQueue();
            User deserialize = SerializationUtil.deserialize(bytes, User.class);
        }
        System.out.println(j+"个对象反序列化耗时："+(System.currentTimeMillis()-l1)/1000.0+"s");


        System.out.println("-------- 边读边写性能测试----------");

        new Thread(() -> {
            int k=0;
            for (int i = 0; i < j; i++) {
                try {
                    User user = new User();
                    user.setName("小王" + i);
                    user.setAge(i);
                    queueSerialization.cacheQueue(SerializationUtil.serialize(user));
                    k=k+i;
                }catch (Exception e){
                }
            }

            System.out.println("写累计数大小："+k);

        }).start();


        new Thread(() -> {
            int k=0;
            for (int i = 0; i < j; i++) {
                try {
                    byte[] bytes = queueSerialization.fetchQueue();
                    User deserialize = SerializationUtil.deserialize(bytes, User.class);
                    k=k+deserialize.getAge();
                }catch (Exception e){
                }
            }

            System.out.println("读累计数大小："+k);
        }).start();

    }
}
