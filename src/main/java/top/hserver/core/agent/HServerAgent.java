package top.hserver.core.agent;

import lombok.extern.slf4j.Slf4j;
import top.hserver.HServerApplication;

import java.io.File;
import java.lang.instrument.Instrumentation;
import java.util.List;

/**
 * @author hxm
 */
@Slf4j
public class HServerAgent {
    public static void agentmain(String args, Instrumentation inst) {

    }

    public static void startAgent() {

        String path = HServerAgent.class.getProtectionDomain().getCodeSource().getLocation().getPath();
        File file = new File(path);
        if (!file.exists()) {
            log.error("热更新开启失败，HServer.jar 查找位置失败");
            return;
        }
        path = file.getAbsolutePath();


//        List<VirtualMachineDescriptor> list = VirtualMachine.list();
//        for (VirtualMachineDescriptor vmd : list) {
//            System.out.println(vmd.displayName());
//            if (vmd.displayName().endsWith("test.hserver.Test")) {
//                VirtualMachine virtualMachine = VirtualMachine.attach(vmd.id());
//                virtualMachine.loadAgent("F:\\Apache\\apache-maven-3.6.2\\res\\top\\hserver\\HServer\\2.9.54\\HServer-2.9.54.jar", "cxs");
//                System.out.println("ok");
//                virtualMachine.detach();
//            }
//        }
//
//        while (true) {
//            Thread.sleep(2000);
//            System.out.println(1);
//        }
    }

}
