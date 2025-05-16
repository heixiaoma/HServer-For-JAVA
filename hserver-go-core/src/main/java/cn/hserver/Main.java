package cn.hserver;

import jnr.ffi.LibraryLoader;
import jnr.ffi.Runtime;

public class Main {
    public static Runtime runtime=null;
    public static void main(String[] args) throws Exception {
        HServerGoCore JNR = LibraryLoader.create(HServerGoCore.class).load("H:\\Java\\HServer\\hserver-go-core\\src\\main\\go-core\\hserver_go_core.dll");
         runtime = Runtime.getRuntime(JNR);
        System.out.println("初始完成");
        JNR.StartProxy(8081, a -> {
//            System.out.println( a);
        });
        System.out.println("调用完成");
        while (true) {
            Thread.sleep(1000);
        }
    }

}