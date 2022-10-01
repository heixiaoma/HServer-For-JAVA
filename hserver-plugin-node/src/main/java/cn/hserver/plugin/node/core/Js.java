package cn.hserver.plugin.node.core;

import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.interop.NodeRuntime;
import com.caoccao.javet.interop.V8Host;
import com.caoccao.javet.interop.converters.JavetProxyConverter;
import com.caoccao.javet.interop.executors.IV8Executor;
import com.caoccao.javet.node.modules.NodeModuleModule;
import com.sun.org.slf4j.internal.Logger;
import com.sun.org.slf4j.internal.LoggerFactory;
import java.io.File;


public class Js {
    private static final Logger log = LoggerFactory.getLogger(Js.class);

    private NodeRuntime nodeRuntime = null;
    private String nodePath;

    public Js(String nodeModulesPath) {
        try {
            nodePath = nodeModulesPath;
            NodeRuntime nodeRuntime = V8Host.getNodeInstance().createV8Runtime();
            nodeRuntime.getNodeModule(NodeModuleModule.class).setRequireRootDirectory(nodeModulesPath);
            JavetProxyConverter javetProxyConverter = new JavetProxyConverter();
            nodeRuntime.setConverter(javetProxyConverter);
            this.nodeRuntime = nodeRuntime;
        } catch (JavetException e) {
            e.printStackTrace();
        }
        init();
    }

    public NodeRuntime getNodeRuntime() {
        return nodeRuntime;
    }

    public IV8Executor run(String script) {
        return nodeRuntime.getExecutor(script);
    }

    public IV8Executor run(File script) {
        try {
            IV8Executor executor = nodeRuntime.getExecutor(script);
            //设置资源目录不然不在同一个目录里执行不了
            executor.setResourceName(nodePath + File.separator + script.getName());
            return executor;
        } catch (JavetException e) {
            log.error(e.getMessage(), e);
        }
        return null;
    }

    public void bind(String key, Object data) {
        try {
            nodeRuntime.getGlobalObject().set(key, data);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    private void init() {
        bind("$node", Node.class);
    }
}
