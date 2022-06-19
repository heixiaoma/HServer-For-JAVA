# hserver-rpc-plugin

## 不错得，纯异步RPC组件，性能管够

```xml
<dependency>
  <groupId>net.hserver.plugins.rpc</groupId>
  <artifactId>hserver-rpc-plugin</artifactId>
  <version>1.0</version>
</dependency>
```

##### 默认模式

```java

//消费者：
@Configuration
public class Config {
    @Bean
    public RpcConfig rpcConfig() {
        //创建一个配置对象
        RpcConfig rpcConfig = new RpcConfig();
        //设置当前RPC模型
        rpcConfig.setRpcAdapter(new DefaultMode());
        //设置提供者得信息，这里是8001服务提供一个provide的服务
        /**
         *      使用这样的代码就能调用provide的接口数据了
         *      @Resource(serverName = "provide")
         *      private Say say;
         */
        RpcServer rpcServer = new RpcServer();
        rpcServer.setHost("127.0.0.1");
        rpcServer.setPort(8001);
        rpcServer.setServerName("provide");
        //将这个服务添加到配置，多个也可以多添加
        rpcConfig.addRpcServer(rpcServer);
        return rpcConfig;
    }

}

//提供者：
@Configuration
public class Config {
    @Bean
    public RpcConfig rpcConfig() {
        RpcConfig rpcConfig = new RpcConfig();
        rpcConfig.setRpcAdapter(new DefaultMode());
        return rpcConfig;
    }

}
//默认模式，消费者直连提供者，无注册中心
```

##### Nacos模式

```java
//消费者 配置两个提供者
@Configuration
public class Config {


    @Bean
    public RpcConfig rpcConfig() {
        RpcConfig rpcConfig = new RpcConfig();

        NacosMode nacosMode = new NacosMode();
        nacosMode.setRegisterAddress("127.0.0.1:8848");
        nacosMode.setRegisterMyIp("127.0.0.1");
        nacosMode.setRegisterMyPort(8002);
        nacosMode.setRegisterName("Consumer");
        rpcConfig.setRpcAdapter(nacosMode);

        RpcServer rpcServer1 = new RpcServer();
        rpcServer1.setIp("127.0.0.1");
        rpcServer1.setPort(8001);
        rpcServer1.setServerName("provide1");

        RpcServer rpcServer2 = new RpcServer();
        rpcServer2.setIp("127.0.0.1");
        rpcServer2.setPort(8003);
        rpcServer2.setServerName("provide2");

        rpcConfig.addRpcServer(rpcServer1);
        rpcConfig.addRpcServer(rpcServer2);
        return rpcConfig;
    }

}
//消费者1
@Configuration
public class Config {

    @Bean
    public RpcConfig rpcConfig() {
        RpcConfig rpcConfig = new RpcConfig();
        NacosMode nacosMode = new NacosMode();
        nacosMode.setRegisterAddress("127.0.0.1:8848");
        nacosMode.setRegisterMyIp("127.0.0.1");
        nacosMode.setRegisterMyPort(8001);
        nacosMode.setRegisterName("provide1");
        rpcConfig.setRpcAdapter(nacosMode);
        return rpcConfig;
    }

}

//消费者2
@Configuration
public class Config {
    @Bean
    public RpcConfig rpcConfig() {
        RpcConfig rpcConfig = new RpcConfig();
        NacosMode nacosMode = new NacosMode();
        nacosMode.setRegisterAddress("127.0.0.1:8848");
        nacosMode.setRegisterMyIp("127.0.0.1");
        nacosMode.setRegisterMyPort(8003);
        nacosMode.setRegisterName("provide2");
        rpcConfig.setRpcAdapter(nacosMode);
        return rpcConfig;
    }

}

//使用Naocs模式可以方便的上下线服务

```

##### 自定义注册中模式
- DefaultMode
- NacosMode

基本流程是先实现 RpcAdapter接口，通过 RpcClient的reg方法进行注册，或者通过remove进行移除（比如nacos的服务上下线就通过remove和重新reg进行操作完成）
可以更具现有的例子用redis实现一个注册中心也是欧克的


```java

public class DefaultMode implements RpcAdapter {

    private static final Logger log = LoggerFactory.getLogger(DefaultMode.class);

    @Override
    public void rpcMode(List<RpcServer> rpcServers, List<String> serverNames) {
        for (RpcServer rpcServer : rpcServers) {
            if (serverNames.contains(rpcServer.getServerName())) {
                RpcClient.reg(rpcServer);
            }else {
                log.warn("{} 服务没用上建议不配置", rpcServer.getServerName());
            }
        }
    }
}



public class NacosMode implements RpcAdapter {

    private static final Logger log = LoggerFactory.getLogger(NacosMode.class);

    //注册中心地址
    private String registerAddress;
    //注册名字
    private String registerName;
    //注册我的Ip
    private String registerMyIp;
    //注册我的端口
    private Integer registerMyPort;

    private String groupName = Constants.DEFAULT_GROUP;

    public void setRegisterAddress(String registerAddress) {
        this.registerAddress = registerAddress;
    }

    public void setRegisterName(String registerName) {
        this.registerName = registerName;
    }

    public void setRegisterMyIp(String registerMyIp) {
        this.registerMyIp = registerMyIp;
    }

    public void setRegisterMyPort(Integer registerMyPort) {
        this.registerMyPort = registerMyPort;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    @Override
    public void rpcMode(List<RpcServer> rpcServers, List<String> serverNames) {

        if (this.registerAddress == null) {
            throw new NullPointerException("Nacos注册地址不能为空");
        }
        if (this.registerName == null) {
            throw new NullPointerException("Nacos注册的名字不能为空");
        }
        if (this.registerMyIp == null) {
            throw new NullPointerException("Nacos注册的自己的IP不能为空");
        }
        if (this.registerMyPort == null) {
            throw new NullPointerException("Nacos注册的自己的Port不能为空");
        }
        try {
            /**
             * nacos 客服端
             */
            NamingService naming = NamingFactory.createNamingService(this.registerAddress);
            naming.registerInstance(this.registerName, this.groupName, this.registerMyIp, this.registerMyPort, this.registerName);

            /**
             * 订阅注册的数据
             */
            subProviderInfo(naming, rpcServers);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void subProviderInfo(NamingService naming, List<RpcServer> rpcServers) {
        /**
         *        按需订阅属于自己的需要的服务
         *
         */
        rpcServers.forEach(regServerName -> {
            try {
                EventListener listener = event -> {
                    if (event instanceof NamingEvent) {
                        NamingEvent evn = (NamingEvent) event;
                        List<Instance> instances = evn.getInstances();
                        log.info("服务变化：" + instances);
                        //节点变化，主动对上下线关系进行清除，重新设置
                        RpcClient.remove(regServerName);
                        for (Instance instance : instances) {
                            RpcServer rpcServer = new RpcServer();
                            rpcServer.setServerName(regServerName.getServerName());
                            rpcServer.setPort(instance.getPort());
                            rpcServer.setIp(instance.getIp());
                            //新变化的节点加入服务
                            RpcClient.reg(rpcServer);
                        }
                    }
                };
                naming.subscribe(regServerName.getServerName(), listener);
            } catch (Exception e) {
                log.warn(e.getMessage());
            }
        });
    }

}

```

