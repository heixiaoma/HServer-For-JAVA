## 打包jar

Main函数类上添加@HServerBoot 注解用于标记是启动类。
只需要在pom.xml 添加打包命令即可，打包之前记得 *clean*

```xml

<build>
    <plugins>
        <plugin>
            <groupId>cn.hserver</groupId>
            <artifactId>hserver-plugin-maven</artifactId>
            <executions>
                <execution>
                    <phase>package</phase>
                    <goals>
                        <goal>repackage</goal>
                    </goals>
                </execution>
            </executions>
        </plugin>
    </plugins>
</build>
```