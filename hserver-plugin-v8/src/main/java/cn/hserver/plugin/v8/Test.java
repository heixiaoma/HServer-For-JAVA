package cn.hserver.plugin.v8;


import cn.hserver.plugin.v8.core.Js;
import com.eclipsesource.v8.JavaCallback;
import com.eclipsesource.v8.V8Array;
import com.eclipsesource.v8.V8Object;
import com.eclipsesource.v8.utils.MemoryManager;

import java.io.File;

public class Test {
    public static void main(String[] args) {
        String path = Test.class.getClassLoader().getResource("js").getPath();
        File main = new File(path + File.separator + "main.js");
        File test = new File(path + File.separator + "test.js");
        Js js = new Js();
        MemoryManager scope = new MemoryManager(js.getRuntime());
        js.registerJavaMethod(new JavaCallback() {
            @Override
            public Object invoke(V8Object v8Object, V8Array v8Array) {
                return "java原始方法";
            }
        },"javaFunction");
        //java 调用js
        V8Object exports = js.getNodeJS().require(test);
        Object test1 = exports.executeJSFunction("test");
        System.out.println(test1);

        //java 执行js
        js.exec(main);
        scope.release();
        js.release();
    }
}
