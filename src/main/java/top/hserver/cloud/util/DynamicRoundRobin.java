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
