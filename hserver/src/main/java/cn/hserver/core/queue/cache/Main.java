package cn.hserver.core.queue.cache;


import org.iq80.leveldb.DB;
import org.iq80.leveldb.DBIterator;
import org.iq80.leveldb.Options;
import org.iq80.leveldb.impl.Iq80DBFactory;

import java.io.File;

import static org.iq80.leveldb.impl.Iq80DBFactory.bytes;

public class Main {

    public static class User {
        private String name;
        private Integer age;

        public User(String name, Integer age) {
            this.name = name;
            this.age = age;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Integer getAge() {
            return age;
        }

        public void setAge(Integer age) {
            this.age = age;
        }

        @Override
        public String toString() {
            return "User{" +
                    "name='" + name + '\'' +
                    ", age=" + age +
                    '}';
        }
    }

    public static void main(String[] args) throws Exception {

        CacheMap<User> db = new CacheMap<>("db", User.class);
        int temp = 1000000;
        long start = System.currentTimeMillis();
        for (int i = 0; i < temp; i++) {
            db.put(String.valueOf(i), new User("aa", i));
        }
        long end = System.currentTimeMillis();
        System.out.println(temp+"次写入耗时："+(end-start)/1000.0+"/s");
        start = System.currentTimeMillis();
        System.out.println(db.get("2000"));
        end = System.currentTimeMillis();
        System.out.println("读取一次耗时："+(end-start)/1000.0+"/s");
    }
}
