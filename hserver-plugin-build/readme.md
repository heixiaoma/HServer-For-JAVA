```xml
<build>
    <!--  默认项目名  -->
    <finalName>aaa.jar</finalName>
    <plugins>
        <plugin>
            <artifactId>hserver-plugin-build</artifactId>
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
