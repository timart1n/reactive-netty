package org.cern.streaming.pool.netty.client;

import cern.streaming.pool.core.service.StreamId;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import org.cern.streaming.pool.netty.client.handler.PublisherProxyConnectionHandler;
import org.cern.streaming.pool.netty.client.handler.PublisherProxyItemHandler;
import org.cern.streaming.pool.netty.domain.*;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Created by timartin on 08/11/2016.
 */
public class NettyPublisherProxy<T> implements Publisher<T> {

    private final int port;
    private final String host;
    private final EventLoopGroup workerGroup;
    private ConnectionState connectionState;
    private Channel channel;
    private final Map<Subscriber<? super T>, Long> subscribers;
    private long itemsAccepted = 0;

    public NettyPublisherProxy(int port, String host, EventLoopGroup workerGroup) {
        this.port = port;
        this.host = host;
        this.workerGroup = workerGroup;
        connectionState = ConnectionState.DISCONNECTED;
        subscribers = new HashMap<>();
    }

    public NettyPublisherProxy(int port, String host, int threadNumber) {
        this(port, host, new NioEventLoopGroup(threadNumber));
    }

    @Override
    public void subscribe(Subscriber<? super T> subscriber) {
        if (!subscribers.containsKey(subscriber)) {
            subscribers.put(subscriber, new Long(0));
        }
        if (!ConnectionState.CONNECTED.equals(connectionState)) {
            connectionState = ConnectionState.CONNECTING;
            Bootstrap channelBootstrap = createChannel();
            try {
                startConnection(channelBootstrap);
            } catch (InterruptedException exception) {
                connectionState = ConnectionState.DISCONNECTED;
            }
        }
    }

    public void handleSubscriptionAnswer(StatusMessage statusMessage) {
        /*if (statusMessage()) {
            connectionState = ConnectionState.DISCONNECTED;
            channel.close();
            for (Subscriber subscriber : subscribers.keySet()) {
                subscriber.onError(new RuntimeException(statusMessage.getStatusMessage()));
            }
        } else {
            for (Subscriber subscriber : subscribers.keySet()) {
                subscriber.onSubscribe(new NettySubscription<>(this, subscriber));
            }
        }*/
    }

    public void request(Subscriber<T> subscriber, long n) {
        Objects.requireNonNull(subscriber, "subscriber");
        if (n <= 0) {
            throw new IllegalArgumentException("While the subscription is not cancelled, requested items must be bigger than 0.");
        }
        if (!channel.isActive()) {
            throw new RuntimeException("Cannot request items on an already closed channel.");
        }
        if (!subscribers.containsKey(subscriber)) {
            throw new RuntimeException("Cannot request items on a for a subscriber that did not subscribe the publisher being requested.");
        }
        Long upbound = subscribers.get(subscriber);
        upbound += n;
        subscribers.put(subscriber, upbound);
        if (upbound > itemsAccepted) {
            channel.writeAndFlush(new RequestMessage(upbound - itemsAccepted));
            itemsAccepted = upbound;
        }
    }

    public void cancel(Subscriber<? super T> subscriber) {
        subscribers.remove(subscriber);
    }

    public void handleItem(ItemMessage<T> item) {
        if (item.isError()) {
            for (Subscriber subscriber : subscribers.keySet()) {
                subscriber.onError(new RuntimeException(item.getStatusMessage()));
            }
        } else {
            if (itemsAccepted > 0) {
                itemsAccepted--;
                for (Subscriber subscriber : subscribers.keySet()) {
                    long upbound = subscribers.get(subscriber);
                    if (upbound > 0) {
                        upbound--;
                        subscriber.onNext(item.getItem());
                        subscribers.put(subscriber, upbound);
                    }
                }
            }
        }
    }

    private Bootstrap createChannel() {
        return new Bootstrap()
                .group(workerGroup)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    public void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new ObjectEncoder());
                        ch.pipeline().addLast(new ObjectDecoder(ClassResolvers.cacheDisabled(getClass().getClassLoader())));
                        ch.pipeline().addLast(new PublisherProxyItemHandler<>(NettyPublisherProxy.this));
                        ch.pipeline().addLast(new PublisherProxyConnectionHandler(NettyPublisherProxy.this));
                    }
                });
    }

    private void startConnection(Bootstrap channelBootstrap) throws InterruptedException {
        ChannelFuture channelFuture = channelBootstrap.connect(host, port);
        channelFuture.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                if (!future.isSuccess()) {
                    connectionState = ConnectionState.DISCONNECTED;
                } else {
                    channel = future.channel();
                    connectionState = ConnectionState.CONNECTED;
                    ChannelFuture channelFuture1 = future.channel().writeAndFlush(new SubscriptionMessage<>(streamId));
                    channelFuture1.addListener(new GenericFutureListener<Future<? super Void>>() {
                        @Override
                        public void operationComplete(Future<? super Void> future) throws Exception {
                            if (!future.isSuccess()) {
                                future.cause().printStackTrace();
                            }
                        }
                    });
                }
            }
        });
    }
}