package cn.hserver.core.ioc.ref;

public class ListBean implements Comparable<ListBean> {

    private Object object;
    private int sort;

    public ListBean(Object object, int sort) {
        this.object = object;
        this.sort = sort;
    }

    public Object getObject() {
        return object;
    }

    public void setObject(Object object) {
        this.object = object;
    }

    public int getSort() {
        return sort;
    }

    public void setSort(int sort) {
        this.sort = sort;
    }

    @Override
    public int compareTo(ListBean o) {
        return 0;
    }
}
