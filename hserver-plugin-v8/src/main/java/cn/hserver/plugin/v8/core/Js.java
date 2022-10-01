package cn.hserver.plugin.v8.core;

import cn.hserver.plugin.v8.utils.FileUtils;
import cn.hserver.plugin.v8.utils.MD5;
import com.eclipsesource.v8.JavaCallback;
import com.eclipsesource.v8.NodeJS;
import com.eclipsesource.v8.V8;
import com.sun.org.slf4j.internal.Logger;
import com.sun.org.slf4j.internal.LoggerFactory;

import java.io.File;

public class Js {
    private static final Logger log = LoggerFactory.getLogger(Js.class);

    private final NodeJS nodeJS;

    public Js(File file) {
        this.nodeJS = NodeJS.createNodeJS(file);
    }

    public Js() {
        this.nodeJS = NodeJS.createNodeJS(null);
    }

    public NodeJS getNodeJS() {
        return nodeJS;
    }

    public V8 getRuntime() {
        return nodeJS.getRuntime();
    }

    /**
     * 像js注册java方法实现 js调用java
     *
     * @param callback
     * @param jsFunctionName
     */
    public void registerJavaMethod(JavaCallback callback, String jsFunctionName) {
        nodeJS.getRuntime().registerJavaMethod(callback, jsFunctionName);
    }

    public void exec(File file) {
        nodeJS.exec(file);
        while (nodeJS.isRunning()) {
            nodeJS.handleMessage();
        }
    }

    public void release(){
        nodeJS.release();
    }

    public void exec(String script) {
        try {
            File temporaryScriptFile = FileUtils.createTemporaryScriptFile(script, MD5.md5(script));
            exec(temporaryScriptFile);
        } catch (Exception e) {
            log.error("创建临时脚本文件失败、暂时不能执行该JS文件：{}\n{}", script, e.getMessage());
        }
    }

}
