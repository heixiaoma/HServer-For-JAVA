package test4;

import top.hserver.core.queue.QueueHandleMethod;

import java.util.*;
import java.util.stream.Collectors;

public class TestFp {

    public static void main(String[] args) {
        List<QueueHandleMethod> eventHandleMethods=new ArrayList<>();
        eventHandleMethods.add(new QueueHandleMethod(null,1,2));
        eventHandleMethods.add(new QueueHandleMethod(null,1,1));
        eventHandleMethods.add(new QueueHandleMethod(null,1,2));
        eventHandleMethods.add(new QueueHandleMethod(null,1,1));
        eventHandleMethods.add(new QueueHandleMethod(null,1,3));
        Map<Integer, List<QueueHandleMethod>> collect = eventHandleMethods.stream().sorted(Comparator.comparingInt(QueueHandleMethod::getLevel)).collect(Collectors.groupingBy(QueueHandleMethod::getLevel));
        collect.forEach((k,v)->{
            System.out.println("----------"+k);
            for (QueueHandleMethod eventHandleMethod : v) {
                System.out.println(eventHandleMethod.getLevel());
            }
        });

    }
}
