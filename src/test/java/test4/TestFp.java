package test4;

import top.hserver.core.event.EventHandleMethod;

import java.util.*;
import java.util.stream.Collectors;

public class TestFp {

    public static void main(String[] args) {
        List<EventHandleMethod> eventHandleMethods=new ArrayList<>();
        eventHandleMethods.add(new EventHandleMethod(null,1,2));
        eventHandleMethods.add(new EventHandleMethod(null,1,1));
        eventHandleMethods.add(new EventHandleMethod(null,1,2));
        eventHandleMethods.add(new EventHandleMethod(null,1,1));
        eventHandleMethods.add(new EventHandleMethod(null,1,3));
        Map<Integer, List<EventHandleMethod>> collect = eventHandleMethods.stream().sorted(Comparator.comparingInt(EventHandleMethod::getLevel)).collect(Collectors.groupingBy(EventHandleMethod::getLevel));
        collect.forEach((k,v)->{
            System.out.println("----------"+k);
            for (EventHandleMethod eventHandleMethod : v) {
                System.out.println(eventHandleMethod.getLevel());
            }
        });

    }
}
