## **RPC调用**

hserver 提供了RpcAdapter类，详细看代码里的实现 支持自定义注册中心完成RPC
目前提供了两种模式 第一种默认模式不需要注册中心 第二种模式是需要Nacos注册中心. 编码过程中没有什么差异
主要差异是在配置上. 文档我怕讲不清楚，详情看项目地址中的 test-rpc-* 案例