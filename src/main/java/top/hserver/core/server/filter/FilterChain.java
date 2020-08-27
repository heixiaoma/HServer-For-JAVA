package top.hserver.core.server.filter;

import top.hserver.core.interfaces.FilterAdapter;
import top.hserver.core.server.context.Webkit;

import java.util.*;

/**
 * @author hxm
 */
public class FilterChain {

    /**
     * FilterChain鏈子
     */
    public final static List<Map<String, FilterAdapter>> FILTERS_IOC = new LinkedList();


    /**
     * 清除所有
     */
    public static synchronized void clearFilterChain() {
        FILTERS_IOC.clear();
    }

    public static FilterChain getFileChain() {
        FilterChain filterChain = new FilterChain();
        List<FilterAdapter> filter = new LinkedList<>();
        for (Map<String, FilterAdapter> filterMap : FILTERS_IOC) {
            Set<String> strings = filterMap.keySet();
            Iterator<String> iterator = strings.iterator();
            if (iterator.hasNext()) {
                filter.add(filterMap.get(iterator.next()));
            }
        }
        filterChain.setFilters(filter);
        return filterChain;
    }


    private List<FilterAdapter> filters;

    private int pos = 0;

    public FilterChain() {
        filters = new LinkedList<>();
    }

    private void setFilters(List<FilterAdapter> filters) {
        this.filters = filters;
    }

    public void doFilter(Webkit webkit) throws Exception {
        if (pos < filters.size()) {
            filters.get(pos++).doFilter(this, webkit);
        }
    }

}