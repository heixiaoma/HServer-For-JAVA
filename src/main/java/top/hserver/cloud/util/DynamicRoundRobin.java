package top.hserver.cloud.util;


import top.hserver.cloud.bean.ServiceData;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author hxm
 */
public class DynamicRoundRobin {

    private final List<ServiceData> list = new CopyOnWriteArrayList<>();

    private AtomicInteger pos = new AtomicInteger(0);

    public void add(ServiceData t) {
        for (int i = 0; i < list.size(); i++) {
            ServiceData serviceData = list.get(i);
            if (serviceData.getName().equals(t.getName())) {
                if (serviceData.getChannel().isActive()) {
                    return;
                } else {
                    list.remove(serviceData);
                    break;
                }
            }
        }
        if (t.getChannel().isActive()) {
            list.add(t);
        }
    }

    public List<ServiceData> getAll() {
        return list;
    }

    public boolean remove(ServiceData t) {
        return list.remove(t);
    }

    public int size() {
        return list.size();
    }

    public ServiceData choose() {
        while (true) {
            int size = list.size();
            if (size == 0) {
                return null;
            }
            int p = pos.getAndIncrement();
            if (p > size - 1) {
                pos.set(0);
                continue;
            }
            try {
                return list.get(p);
            } catch (IndexOutOfBoundsException e) {
                //有可能在取的过程中，list被删除元素了，所以重置一下，重新轮询。
                pos.set(0);
            }
        }
    }
}
