package cn.hserver.plugin.rpc.codec;

import cn.hserver.plugin.rpc.client.ChannelPool;
import cn.hserver.plugin.rpc.config.RpcConfig;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import cn.hserver.core.ioc.IocUtil;

public class ServiceData {

    private String serverName;

    private String ip;

    private Integer port;

    private ChannelPool channelPool;

    public String getServerName() {
        return serverName;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public ChannelPool getChannelPool() {
        return channelPool;
    }

    public synchronized void initChannelPool() {
        if (this.channelPool == null || this.channelPool.isClosed()) {
            GenericObjectPoolConfig config = new GenericObjectPoolConfig();
            RpcConfig bean = IocUtil.getBean(RpcConfig.class);
            if (bean != null) {
                config.setMaxIdle(bean.getMaxIdle());//最大活跃数
                config.setMinIdle(bean.getMinIdle());//最小活跃数
                config.setMaxTotal(bean.getMaxTotal());//最大总数
            }
            this.channelPool = new ChannelPool(config, this.ip, this.port);
        }
    }

    public synchronized void closeChannelPool() {
        if (this.channelPool != null && this.channelPool.isClosed()) {
            this.channelPool.close();
        }
    }
}