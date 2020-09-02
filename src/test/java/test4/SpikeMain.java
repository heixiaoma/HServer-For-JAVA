package test4;

import test1.bean.User;

public class SpikeMain {
    public static void main(String[] args) throws Exception{
        for (int i = 0; i < 10; i++) {
            User user=new User();
            user.setName("666");
            user.setAge(i);
            DisruptorUtils.producer(user,user);
        }
        Thread.sleep(5000);
    }
}
