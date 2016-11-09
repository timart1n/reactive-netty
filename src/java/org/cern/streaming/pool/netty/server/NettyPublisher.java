package org.cern.streaming.pool.netty.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import org.cern.streaming.pool.netty.server.handler.PublisherConnectionHandler;
import org.cern.streaming.pool.netty.server.handler.PublisherRequestHandler;
import org.reactivestreams.Publisher;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by timartin on 08/11/2016.
 */
public abstract class NettyPublisher<T> implements Publisher<T> {

    private final String host;
    private final int port;
    private final EventLoopGroup workerGroup;
    private final Map<Channel, NettySubscriberProxy<T>> subscribers;

    public NettyPublisher(String host, int port, EventLoopGroup workerGroup) {
        this.host = host;
        this.port = port;
        this.workerGroup = workerGroup;
        subscribers = new HashMap<>();
    }

    public NettyPublisher(String host, int port, int numThreads) {
        this(host, port, new NioEventLoopGroup(numThreads));
    }

    public ChannelFuture bindChannel() throws InterruptedException {
        ServerBootstrap bootstrap = createDefaultChannel();
        return bindChannel(bootstrap);
    }

    public ChannelFuture bindChannel(ServerBootstrap channelBootstrap) throws InterruptedException {
        return channelBootstrap.bind(port);
    }

    private ServerBootstrap createDefaultChannel() {
        return new ServerBootstrap()
                .group(workerGroup)
                .channel(NioServerSocketChannel.class)
                .handler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new ChannelInboundHandlerAdapter() {
                            @Override
                            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                super.channelRead(ctx, msg);
                            }
                        });
                    }
                }).childHandler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    public void initChannel(NioSocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new ObjectEncoder());
                        ch.pipeline().addLast(new ObjectDecoder(ClassResolvers.cacheDisabled(getClass().getClassLoader())));
                        ch.pipeline().addLast(new PublisherRequestHandler(NettyPublisher.this));
                        ch.pipeline().addLast(new PublisherConnectionHandler<>(NettyPublisher.this));
                    }
                });
    }

    public void addSubscriber(NettySubscriberProxy<T> subscriber) {
        if (!subscribers.containsKey(subscriber)) {
            subscribers.put(subscriber.getChannel(), subscriber);
            subscriber.onSubscribe(null);
            subscribe(subscriber);
        }
    }

    ;

    public void request(long requestedNumber, Channel channel) {
        subscribers.get(channel).request(requestedNumber);
    }
}
