package cn.hserver.core.ioc;

import cn.hserver.core.ioc.annotation.Component;

import javax.sql.DataSource;

public class UserService {

    private final DataSource dataSource;

    public UserService(DataSource dataSource) {
        this.dataSource=dataSource;
    }

    public DataSource getDataSource() {
        return dataSource;
    }
}
