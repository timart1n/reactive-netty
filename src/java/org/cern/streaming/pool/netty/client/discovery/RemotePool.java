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
import org.cern.streaming.pool.netty.client.discovery.handler.StreamInfoAnswerHandler;
import org.cern.streaming.pool.netty.client.discovery.handler.StreamInfoRequestFuture;
import org.cern.streaming.pool.netty.domain.RemoteReactiveStream;
import org.cern.streaming.pool.netty.domain.StreamInfoRequest;

public class RemotePool implements DiscoveryService {

    private static final String REACTIVE_STRING = "REACTIVE_STREAM";

    private final int port;
    private final String host;
    private final EventLoopGroup workerGroup;

    public RemotePool(int port, String host, EventLoopGroup workerGroup) {
        this.port = port;
        this.host = host;
        this.workerGroup = workerGroup;
    }

    @Override
    public <T> ReactiveStream<T> discover(StreamId<T> id) {
        try {
            StreamInfoRequestFuture streamInfoRequestFuture = new StreamInfoRequestFuture();
            RemoteReactiveStream<T> reactiveStream = new RemoteReactiveStream<>(streamInfoRequestFuture);
            ChannelFuture channelFuture = createChannel(streamInfoRequestFuture);
            channelFuture.addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture future) throws Exception {
                    if (future.isSuccess()) {
                        Channel channel = channelFuture.channel();
                        channel.writeAndFlush(new StreamInfoRequest<>(id));
                    }
                }
            });
            return reactiveStream;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    private final ChannelFuture createChannel(StreamInfoRequestFuture streamInfoRequestFuture) throws InterruptedException {
        Bootstrap bootstrap = createBootstrap(streamInfoRequestFuture);
        return bootstrap.connect(host, port);
    }

    private final <T> Bootstrap createBootstrap(StreamInfoRequestFuture streamInfoRequestFuture) {
        return new Bootstrap()
                .group(workerGroup)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    public void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new ObjectEncoder());
                        ch.pipeline().addLast(new ObjectDecoder(ClassResolvers.cacheDisabled(getClass().getClassLoader())));
                        ch.pipeline().addLast(new StreamInfoAnswerHandler<>(streamInfoRequestFuture));
                    }
                });
    }
}
