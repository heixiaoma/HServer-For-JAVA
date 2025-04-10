package cn.hserver;

import jnr.ffi.LibraryLoader;

public class Main {

    public static void main(String[] args) throws Exception {
        HServerGoCore JNR = LibraryLoader.create(HServerGoCore.class).load("E:\\code\\java\\HServer\\hserver-go-core\\src\\main\\go-core\\hserver_go_core.dll");
        System.out.println("初始完成");
        JNR.StartProxy(8081, a -> {
//            System.out.println("请求地址:" + a);
        });
        System.out.println("调用完成");
        while (true) {
            Thread.sleep(1000);
        }
    }

}