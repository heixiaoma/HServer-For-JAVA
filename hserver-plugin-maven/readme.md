
### 打包模块
- 加密模式情况
- - 参数设置密码启动：java -Dpassword=dm_pro_token_key -jar xxx.jar
- - 命令行提示输入密码 

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
                        <!--         输入密码不会有记录，长用于手动输入，日志不会记录，不方便第三方接入，至少需要伪终端模拟               -->
                        <!--         如果设置false，通常可用于第三方程序输入，方便process stream 连接调用               -->
                        <console>true</console>
                    </configuration>
                </execution>
            </executions>
        </plugin>
    </plugins>
</build>
```
