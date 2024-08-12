package cn.hserver.plugin.node;

import cn.hserver.plugin.node.config.ConstConfig;
import cn.hserver.plugin.node.core.Js;
import com.caoccao.javet.interop.NodeRuntime;
import com.caoccao.javet.interop.executors.IV8Executor;

import java.io.File;

public class Test {
    public static void main(String[] args) throws Exception {
        Js js = new Js(ConstConfig.PATH + "v8/js/node_modules");
        File main = new File(ConstConfig.PATH + "v8/js/main.js");
        IV8Executor run = js.run(main);
        try {
            NodeRuntime nodeRuntime = js.getNodeRuntime();
            run.executeVoid();
            nodeRuntime.await();
        } catch (Exception e) {
            e.printStackTrace();
        }


    }
}
