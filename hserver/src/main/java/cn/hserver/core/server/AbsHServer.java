package cn.hserver.core.server;

import cn.hserver.core.server.context.ConstConfig;
import cn.hserver.core.server.handlers.HumClientHandler;
import cn.hserver.core.server.handlers.HumServerHandler;
import cn.hserver.core.server.util.EpollUtil;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.epoll.EpollChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.util.internal.ObjectUtil;

import java.util.LinkedHashMap;
import java.util.Map;

import static cn.hserver.core.server.context.ConstConfig.HUM_PORT;

public abstract class AbsHServer {
    /**
     * option
     */
    private final Map<ChannelOption<Object>, Object> options = new LinkedHashMap<>();
    private final Map<ChannelOption<Object>, Object> childOption = new LinkedHashMap<>();

    private ServerType serverType;

    protected void setServerType(ServerType serverType) {
        this.serverType = serverType;
        switch (serverType) {
            case TCP: {
                if (EpollUtil.check()) {
                    option(EpollChannelOption.SO_REUSEPORT, true);
                }
                childOption(ChannelOption.SO_KEEPALIVE, true);
                childOption(ChannelOption.TCP_NODELAY, true);
            }
            case UDP: {
                option(ChannelOption.SO_BROADCAST, true);
                option(EpollChannelOption.SO_REUSEPORT, true);
            }
        }
    }

    public void start() {
        try {
            if (ConstConfig.HUM_OPEN) {

                new HNetty(ServerType.UDP, new Integer[]{HUM_PORT}, options, childOption);
                final HNetty udpClient = new HNetty(ServerType.UDP, new Integer[]{0}, options, childOption);
                udpClient.getChannels().forEach((k, v) -> {
                    HumClient.channel = k;
                });
            }
            final HNetty hNetty = new HNetty(serverType, ConstConfig.PORTS, options, childOption);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public <T> AbsHServer option(ChannelOption<T> option, T value) {
        ObjectUtil.checkNotNull(option, "option");
        synchronized (this.options) {
            if (value == null) {
                this.options.remove(option);
            } else {
                this.options.put((ChannelOption) option, value);
            }
        }
        return this;
    }

    public <T> AbsHServer childOption(ChannelOption<T> option, T value) {
        ObjectUtil.checkNotNull(option, "option");
        synchronized (this.childOption) {
            if (value == null) {
                this.childOption.remove(option);
            } else {
                this.childOption.put((ChannelOption) option, value);
            }
        }
        return this;
    }

}
