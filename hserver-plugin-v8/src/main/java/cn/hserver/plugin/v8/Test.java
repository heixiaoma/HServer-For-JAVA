package cn.hserver.plugin.v8;


import cn.hserver.plugin.v8.core.Js;
import com.eclipsesource.v8.JavaCallback;
import com.eclipsesource.v8.V8Array;
import com.eclipsesource.v8.V8Object;

import java.io.File;

public class Test {
    public static void main(String[] args) {
        String path = Test.class.getClassLoader().getResource("js").getPath();
        File main = new File(path + File.separator + "main.js");
        Js js = new Js();
        js.registerJavaMethod(new JavaCallback() {
            @Override
            public Object invoke(V8Object v8Object, V8Array v8Array) {
                return "java原始方法";
            }
        },"javaFunction");
        js.exec(main);
        js.release();
    }
}
