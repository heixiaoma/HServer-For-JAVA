
## 压测

    通常不可能跑一个hello程序，都会伴随着业务，压测截图只是参考，框架qps本身很高,就看你怎么玩了

### 环境Win（Nio模型）
    win10,i7_9700k(8核16G)（每秒10W次左右）
##### 1.压测json
![AB测试](https://gitee.com/heixiaomas_admin/HServer/raw/master/doc/json.png)
##### 2.压测静态文件
![AB测试](https://gitee.com/heixiaomas_admin/HServer/raw/master/doc/file.png)
##### 3.模板引擎html
![AB测试](https://gitee.com/heixiaomas_admin/HServer/raw/master/doc/template.png)


### 环境Linux（Epoll模型）
    阿里云学生机 Centos7.3（1核2G内存）（每秒6W次左右）
##### 1.压测json
![AB测试](https://gitee.com/heixiaomas_admin/HServer/raw/master/doc/LinuxJson.png)
##### 2.压测静态文件
![AB测试](https://gitee.com/heixiaomas_admin/HServer/raw/master/doc/LinuxFile.png)
##### 3.模板引擎html
![AB测试](https://gitee.com/heixiaomas_admin/HServer/raw/master/doc/LinuxTemp.png)


### HServer2.2版本，环境Linux（Epoll模型）
    配置阿里云学生机 Centos7.3（1核2G内存）
    hello--> Requests per second:    73156.87 [#/sec] (mean)
    reids读--> Requests per second:    44103.39 [#/sec] (mean)
    redis写--> Requests per second:    38631.04 [#/sec] (mean) 
文档源码地址 [点我](https://gitee.com/heixiaomas_admin/hserver-for-java-redis)

### Hserver2.4版本，环境Linux（1核2G）
    RPC远程调用测试：Requests per second:    15709.09 [#/sec] (mean)
 
 
### Websocket压测
    /**
        压测代码
    */
    <?php
    require __DIR__ . '/../Workerman/Autoloader.php';
    use Workerman\Worker;
    use Workerman\Lib\Timer;
    use Workerman\Connection\AsyncTcpConnection;
    $worker = new Worker();
    $worker->onWorkerStart = 'connect';
    function connect(){
        static $count = 0;
        // 2000个链接
        if ($count++ >= 10000) return;
        // 建立异步链接
        $con = new AsyncTcpConnection('ws://127.0.0.1:8888/ws');
        $con->onConnect = function($con) {
            // 递归调用connect
            connect();
        };
        $con->onMessage = function($con, $msg) {
            echo "recv $msg\n";
        };
        $con->onClose = function($con) {
            echo "con close\n";
        };
        // 当前链接每10秒发个心跳包
        Timer::add(10, function()use($con){
            $con->send("ping");
        });
        $con->connect();
        echo $count, " connections complete\n";
    }
    Worker::runAll();
    
    /**
        压测结果
        10000个连接，收发消息正常，理论应该可以更难高，压测时间太久了
        测试环境，腾讯云服务器压测阿里云服务器，配置都为1核2G.
    */
    
    
### HServer2.9.4版本，环境Linux（2核4G）
    #服务器同时器了其他程序，一个java项目，activeMq，redis，mysql（基本处于未访问状态，但是对测试肯定还是有一些的影响）
    RPC远程调用测试：Requests per second:    16709.09 [#/sec] (mean)
    Json测试：Requests per second:    65711.15 [#/sec] (mean)
    Json+AOP测试：Requests per second:    61054.15 [#/sec] (mean)
    静态文件测试：Requests per second:    35843.15 [#/sec] (mean)
    模板文件测试：Requests per second:    51492.05 [#/sec] (mean)

###  HServer2.9.9版本，环境Linux（8核2G虚拟机）
![AB测试](https://gitee.com/heixiaomas_admin/HServer/raw/master/doc/8.png)

###  HServer2.9.10版本，环境Linux（8核2G虚拟机）
    数据只做参考，这玩意悬乎得很，一切都应该以实际业务为准，如有问题可以及时联系作者反馈
![AB测试](https://gitee.com/heixiaomas_admin/HServer/raw/master/doc/23.png)

###  HServer Jmeter压测
![AB测试](https://gitee.com/heixiaomas_admin/HServer/raw/master/doc/jm.png)