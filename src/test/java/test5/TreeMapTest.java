package test5;

import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicInteger;

public class TreeMapTest {

    public static void main(String[] args) {
         TreeMap<String, AtomicInteger> blockInfo = new TreeMap<>();
         blockInfo.put("a",new AtomicInteger(1));
         blockInfo.put("b",new AtomicInteger(1));
         blockInfo.put("c",new AtomicInteger(1));

        Map.Entry<String, AtomicInteger> stringAtomicIntegerEntry = blockInfo.firstEntry();
        String key = stringAtomicIntegerEntry.getKey();
        System.out.println(key);
        blockInfo.put("d",new AtomicInteger(1));

        stringAtomicIntegerEntry = blockInfo.firstEntry();
        key = stringAtomicIntegerEntry.getKey();
        System.out.println(key);

    }
}
