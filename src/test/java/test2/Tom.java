package test2;

import top.hserver.core.ioc.annotation.ConfigurationProperties;

@ConfigurationProperties(prefix = "a")
public class Tom {
    private String name;
    private Integer age;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    @Override
    public String toString() {
        return "Tom{" +
                "name='" + name + '\'' +
                ", age=" + age +
                '}';
    }
}
