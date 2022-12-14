package cn.hserver.plugin.cloud;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public class DynamicRoundRobin {

    private final List<ServerInstance> list = new CopyOnWriteArrayList<>();

    private final AtomicInteger pos = new AtomicInteger(0);

    public DynamicRoundRobin() {

    }

    public DynamicRoundRobin(ServerInstance t) {
        list.add(t);
    }

    public void add(ServerInstance t) {
        if (t.isHealthy()) {
            for (ServerInstance serverInstance : list) {
                if (serverInstance.getEq().equals(t.getEq())) {
                    return;
                }
            }
            list.add(t);
        } else {
            remove(t);
        }
    }

    public List<ServerInstance> getAll() {
        return list;
    }

    public boolean remove(ServerInstance t) {
        return list.remove(t);
    }

    public void removeAll() {
        for (int i = 0; i < list.size(); i++) {
            list.remove(i);
        }
    }

    public int size() {
        return list.size();
    }

    public ServerInstance choose() {
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