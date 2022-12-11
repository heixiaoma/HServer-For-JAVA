package cn.hserver.core.server.util;

import cn.hserver.HServerApplication;
import io.netty.util.internal.PlatformDependent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.util.Arrays;
import java.util.List;

public class JvmStack {

    private static final Logger log = LoggerFactory.getLogger(JvmStack.class);


    private static final long MB = 1048576L;

    public static void printMemoryInfo() {
        MemoryMXBean memory = ManagementFactory.getMemoryMXBean();
        MemoryUsage headMemory = memory.getHeapMemoryUsage();

        String info = String.format("\n初始: %s\t 最大: %s\t 已经使用: %s\t 交给虚拟机使用的: %s\t 使用率: %s\n",
                headMemory.getInit() / MB + "MB",
                headMemory.getMax() / MB + "MB", headMemory.getUsed() / MB + "MB",
                headMemory.getCommitted() / MB + "MB",
                headMemory.getUsed() * 100 / headMemory.getCommitted() + "%"

        );
        log.info(info);
        MemoryUsage nonheadMemory = memory.getNonHeapMemoryUsage();

        info = String.format("初始: %s\t 最大: %s\t 已经使用: %s\t 交给虚拟机使用的: %s\t  使用率: %s\n",
                nonheadMemory.getInit() / MB + "MB",
                nonheadMemory.getMax() / MB + "MB", nonheadMemory.getUsed() / MB + "MB",
                nonheadMemory.getCommitted() / MB + "MB",
                nonheadMemory.getUsed() * 100 / nonheadMemory.getCommitted() + "%"

        );
        log.info(info);

        //打印堆外内存
        long maxDirectMemory = PlatformDependent.maxDirectMemory();
        long usedDirectMemory = PlatformDependent.usedDirectMemory();
        info = String.format("堆外内存 最大: %s\t 已经使用: %s\n",
                maxDirectMemory / MB + "MB",
                usedDirectMemory / MB + "MB"
        );
        log.info(info);
    }

    public static void printGCInfo() {
        List<GarbageCollectorMXBean> garbages = ManagementFactory.getGarbageCollectorMXBeans();
        for (GarbageCollectorMXBean garbage : garbages) {
            String info = String.format("名字: %s\t 次数:%s\t 搜集时间:%s\t 内存池名字:%s",
                    garbage.getName(),
                    garbage.getCollectionCount(),
                    garbage.getCollectionTime(),
                    Arrays.deepToString(garbage.getMemoryPoolNames()));
            log.info(info);
        }
    }

}