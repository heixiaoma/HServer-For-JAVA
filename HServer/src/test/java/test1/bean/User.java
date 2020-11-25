package test1.bean;

import com.fasterxml.jackson.annotation.JsonIgnore;
import test1.log.Log;
import top.hserver.core.ioc.annotation.validate.Length;
import top.hserver.core.ioc.annotation.validate.NotNull;
import top.hserver.core.ioc.annotation.validate.Null;

import java.util.List;

public class User {

    @Length(1)
    private String name;
    @NotNull
    private String sex;
    private Integer age;
    private List<String> test;


    public List<String> getTest() {
        return test;
    }

    public void setTest(List<String> test) {
        this.test = test;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    @Override
    public String toString() {
        return "User{" +
                "name='" + name + '\'' +
                ", sex='" + sex + '\'' +
                ", age=" + age +
                ", test=" + test +
                '}';
    }
}
