package cn.hserver.core.ioc;

import cn.hserver.core.config.annotation.ConfigurationProperties;

@ConfigurationProperties(prefix = "test")
public class ConfigTest {

    private String name;
    private Double height;
    private Integer age;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getHeight() {
        return height;
    }

    public void setHeight(Double height) {
        this.height = height;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }
}
