package org.cern.streaming.pool.netty.client.discovery;

import cern.streaming.pool.core.service.DiscoveryService;
import cern.streaming.pool.core.service.ReactiveStream;
import cern.streaming.pool.core.service.StreamId;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import org.cern.streaming.pool.netty.client.handler.PublisherProxyConnectionHandler;
import org.cern.streaming.pool.netty.client.handler.PublisherProxyItemHandler;
import org.cern.streaming.pool.netty.domain.SubscriptionMessage;

/**
 * Created by timartin on 09/11/2016.
 */
public class RemotePool implements DiscoveryService {

    private final int port;
    private final String host;
    private final EventLoopGroup workerGroup;
    private Channel channel;

    public RemotePool(int port, String host, EventLoopGroup workerGroup) {
        this.port = port;
        this.host = host;
        this.workerGroup = workerGroup;
    }

    @Override
    public <T> ReactiveStream<T> discover(StreamId<T> id) {
        ChannelFuture channelFuture = channel.writeAndFlush(new SubscriptionMessage<>(id));
        final ReactiveStream<T> stream = null;
        channelFuture.addListener(new GenericFutureListener<Future<? super Void>>() {
            @Override
            public void operationComplete(Future<? super Void> future) throws Exception {
            }
        });
        return null;
    }

    private final void createChannel() throws InterruptedException {
        Bootstrap bootstrap = createBootstrap();
        ChannelFuture channelFuture = bootstrap.connect(host, port);
        channelFuture.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                if(future.isSuccess()) {
                    channel = future.channel();
                }
            }
        });
        channelFuture.sync();
    }

    private Bootstrap createBootstrap() {
        return new Bootstrap()
                .group(workerGroup)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    public void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new ObjectEncoder());
                        ch.pipeline().addLast(new ObjectDecoder(ClassResolvers.cacheDisabled(getClass().getClassLoader())));
                    }
                });
    }

}
