package test5;

import test1.bean.User;
import top.hserver.cloud.util.SerializationUtil;
import top.hserver.core.queue.QueueSerialization;

public class TestWriteRead {
    public static void main(String[] args) throws Exception {
        QueueSerialization queueSerialization = new QueueSerialization();

        System.out.println("-------- 边读边写性能测试----------");
        int j=1000;
        new Thread(() -> {
            int k=0;
            for (int i = 0; i < j; i++) {
                try {
                    User user = new User();
                    user.setAge(i);
                    user.setName("小王" + i);
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
