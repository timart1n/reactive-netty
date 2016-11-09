package org.cern.streaming.pool.netty.server.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.cern.streaming.pool.netty.domain.SubscriptionMessage;
import org.cern.streaming.pool.netty.server.NettyPublisher;
import org.cern.streaming.pool.netty.server.NettySubscriberProxy;

/**
 * Created by timartin on 09/11/2016.
 */
public class PublisherConnectionHandler<T> extends SimpleChannelInboundHandler<SubscriptionMessage<?>> {

    private final NettyPublisher<T> publisher;

    public PublisherConnectionHandler(NettyPublisher<T> publisher) {
        this.publisher = publisher;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, SubscriptionMessage<?> msg) throws Exception {
        NettySubscriberProxy<T> subscriber = new NettySubscriberProxy<>(publisher, ctx.channel());
        publisher.addSubscriber(subscriber);
    }
}
