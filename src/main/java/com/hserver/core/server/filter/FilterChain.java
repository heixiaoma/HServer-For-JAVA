package com.hserver.core.server.filter;

import com.hserver.core.interfaces.FilterAdapter;
import com.hserver.core.server.context.Webkit;

import java.util.*;

public class FilterChain {

    /**
     * FilterChain鏈子
     */
    public final static List<Map<String, FilterAdapter>> filtersIoc = new LinkedList();

    public static FilterChain getFileChain() {
        FilterChain filterChain = new FilterChain();
        List<FilterAdapter> filter = new LinkedList<>();
        for (Map<String, FilterAdapter> filterMap : filtersIoc) {
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

    public void setFilters(List<FilterAdapter> filters) {
        this.filters = filters;
    }

    public void doFilter(Webkit webkit) {
        if (pos < filters.size()) {
            filters.get(pos++).doFilter(this, webkit);
        }
    }

}