## 打包jar

Main函数类上添加@HServerBoot 注解用于标记是启动类。
```xml
<build>
    <!--  默认项目名  -->
    <finalName>aaa</finalName>
    <plugins>
        <plugin>
            <artifactId>hserver-plugin-maven</artifactId>
            <groupId>cn.hserver</groupId>
            <executions>
                <execution>
                    <configuration>
                        <!--         默认胖包               -->
                        <fatJar>true</fatJar>
                    </configuration>
                    <configuration>
                        <!--          默认没有密码              -->
                        <password>dm_pro_token_key</password>
                    </configuration>
                </execution>
            </executions>
        </plugin>
    </plugins>
</build>
```