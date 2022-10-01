package cn.hserver.plugin.v8.utils;

import java.math.BigInteger;
import java.security.MessageDigest;

public class MD5 {
    public static String md5(String data) {
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");//申明使用MD5算法
            md5.update(data.getBytes());//
            return new BigInteger(1, md5.digest()).toString(16);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
