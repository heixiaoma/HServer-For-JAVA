package cn.hserver.plugin.rpc.client;

import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.PooledObjectFactory;
import org.apache.commons.pool2.impl.DefaultPooledObject;

/**
 * 通道工厂
 */
public class ChannelFactory implements PooledObjectFactory<NettyChannel> {
    private final String host;
    private final int port;

    ChannelFactory(String host,int port){
        this.host=host;
        this.port=port;
    }
    @Override
    public void activateObject(PooledObject<NettyChannel> nettyChannel) throws Exception {

    }
    @Override
    public void destroyObject(PooledObject<NettyChannel> nettyChannel) throws Exception{

    }
    @Override
    public PooledObject<NettyChannel> makeObject() throws Exception {
        NettyChannel conn=new NettyChannel(host,port);
        return new DefaultPooledObject<NettyChannel>(conn);
    }
    @Override
    public void passivateObject(PooledObject<NettyChannel> nettyChannel) throws Exception {
        // TODO maybe should select db 0? Not sure right now.
    }
    @Override
    public boolean validateObject(PooledObject<NettyChannel> nettyChannel) {
        return true;
    }

}