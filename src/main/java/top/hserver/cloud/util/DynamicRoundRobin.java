package top.hserver.cloud.util;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public class DynamicRoundRobin<T> {
  private final List<T> list = new CopyOnWriteArrayList<>();
  private AtomicInteger pos = new AtomicInteger(0);

  public void add(T t) {
    if (!list.contains(t)) {
      list.add(t);
    }
  }

  public boolean remove(T t) {
    return list.remove(t);
  }

  public int size() {
    return list.size();
  }

  public T choose() {
    while (true) {
      if (list.size()==0){
        return null;
      }
      if (pos.intValue() < list.size()) {
        synchronized (list){
          return list.get(pos.getAndIncrement());
        }
      } else {
        pos.set(0);
      }
    }
  }
}
