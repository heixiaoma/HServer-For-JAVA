package net.hserver.plugin.rpc.client;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

/**
 * 通道池
 */
public class ChannelPool  extends Pool<NettyChannel> {

    public ChannelPool(final GenericObjectPoolConfig poolConfig, final String host, final int port) {
        super(poolConfig,new ChannelFactory(host,port));
    }
    public ChannelPool( final String host, final int port) {
        super(new GenericObjectPoolConfig(),new ChannelFactory(host,port));
    }
    @Override
    public NettyChannel getResource() {
        NettyChannel connection = super.getResource();
        return connection;
    }
    @Override
    public void returnBrokenResource(final NettyChannel resource) {
        if (resource != null) {
            returnBrokenResourceObject(resource);
        }
    }
    @Override
    public void returnResource(final NettyChannel resource) {
        if (resource != null) {
            try {
                returnResourceObject(resource);
            } catch (Exception e) {
                returnBrokenResource(resource);
            }
        }
    }
    
}