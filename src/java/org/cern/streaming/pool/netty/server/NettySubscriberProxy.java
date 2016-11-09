package org.cern.streaming.pool.netty.server;

import io.netty.channel.Channel;
import org.cern.streaming.pool.netty.domain.ItemMessage;
import org.cern.streaming.pool.netty.domain.StatusMessage;
import org.cern.streaming.pool.netty.server.NettyPublisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.io.Serializable;


/**
 * Created by timartin on 09/11/2016.
 */
public class NettySubscriberProxy<T> implements Subscriber<T> {

    private final NettyPublisher<T> publisher;
    private final Channel channel;
    private long itemsAccepted = 0;

    public NettySubscriberProxy(NettyPublisher<T> publisher, Channel channel) {
        this.publisher = publisher;
        this.channel = channel;
    }

    @Override
    public void onSubscribe(Subscription s) {
        checkChannel();
        channel.writeAndFlush(StatusMessage.ACCEPTED());
    }

    @Override
    public void onNext(T t) {
        checkChannel();
        if(itemsAccepted > 0) {
            itemsAccepted--;
            channel.writeAndFlush(new ItemMessage<>(t));
        }
    }

    @Override
    public void onError(Throwable t) {
        checkChannel();
        channel.writeAndFlush(new StatusMessage(2, t.getMessage()));
    }

    @Override
    public void onComplete() {
        checkChannel();
        channel.write(StatusMessage.COMPLETE());
    }

    private void checkChannel() {
        if(!channel.isActive()) {
            throw new IllegalStateException("onSubscribe cannot be sent on an already closed channel.");
        }
    }

    void request(long requestedNumber) {
        System.out.println("Added " + requestedNumber);
        itemsAccepted += requestedNumber;
    }

    Channel getChannel() {
        return channel;
    }
}