package top.hserver.core.agent;

import com.sun.tools.attach.VirtualMachine;
import com.sun.tools.attach.VirtualMachineDescriptor;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.filefilter.HiddenFileFilter;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.monitor.FileAlterationMonitor;
import org.apache.commons.io.monitor.FileAlterationObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.hserver.core.server.context.ConstConfig;

import java.io.File;
import java.lang.instrument.Instrumentation;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author hxm
 */
public class HServerAgent {
    private static final Logger log = LoggerFactory.getLogger(HServerAgent.class);

    public static void agentmain(String args, Instrumentation inst) {

        log.debug("监控文件夹：" + ConstConfig.CLASSPATH);
        // 轮询间隔 1 秒
        long interval = TimeUnit.SECONDS.toMillis(1);
        // 创建过滤器
        IOFileFilter directories = FileFilterUtils.and(
                FileFilterUtils.directoryFileFilter(),
                HiddenFileFilter.VISIBLE);
        IOFileFilter files = FileFilterUtils.and(
                FileFilterUtils.fileFileFilter(),
                FileFilterUtils.suffixFileFilter(".class"));
        IOFileFilter filter = FileFilterUtils.or(directories, files);
        // 使用过滤器
        FileAlterationObserver observer = new FileAlterationObserver(new File(ConstConfig.CLASSPATH), filter);
        //不使用过滤器
        //FileAlterationObserver observer = new FileAlterationObserver(new File(rootDir));
        observer.addListener(new FileListener(inst));
        //创建文件变化监听器
        FileAlterationMonitor monitor = new FileAlterationMonitor(interval, observer);
        // 开始监控
        try {
            monitor.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static boolean ST = false;

    public static void startAgent(Class clazz) {
        if (ST) {
            return;
        }
        ST = true;
        if (clazz == null) {
            log.error("热更新开启失败，未指定主函数class");
            return;
        }

        String path = HServerAgent.class.getProtectionDomain().getCodeSource().getLocation().getPath();
        File file = new File(path);
        if (!file.exists()) {
            log.error("热更新开启失败，HServer.jar 查找位置失败");
            return;
        }
        path = file.getAbsolutePath();

        try {
            List<VirtualMachineDescriptor> list = VirtualMachine.list();
            for (VirtualMachineDescriptor vmd : list) {
                if (vmd.displayName().endsWith(clazz.getName())) {
                    VirtualMachine virtualMachine = VirtualMachine.attach(vmd.id());
                    virtualMachine.loadAgent(path, "cxs");
                    virtualMachine.detach();
                }
            }
            log.info("热更新 Agent 附加成功");
        } catch (Throwable e) {
            log.error("热更新开启失败");
            log.error("默认环境检测失败，请导入 jdk中的 tools.jar");
            System.out.println("===========导入pom.xm 例子===============");
            System.out.println("<dependency>");
            System.out.println("    <groupId>com.sun</groupId>");
            System.out.println("    <artifactId>tools</artifactId>");
            System.out.println("    <version>1.8</version>");
            System.out.println("    <scope>system</scope>");
            System.out.println("    <systemPath>${env.JAVA_HOME}/lib/tools.jar</systemPath>");
            System.out.println("    <optional>true</optional>");
            System.out.println("</dependency>");
            System.out.println("========================================");
        }
    }
}
