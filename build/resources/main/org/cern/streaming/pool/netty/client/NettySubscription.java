package org.cern.streaming.pool.netty.client;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.io.Serializable;


/**
 * Created by timartin on 25/10/2016.
 */
public class NettySubscription<T> implements Subscription {

    private final NettyPublisherProxy<T> publisher;
    private final Subscriber<T> subscriber;

    public NettySubscription(NettyPublisherProxy<T> publisher, Subscriber<T> subscriber) {
        this.publisher = publisher;
        this.subscriber = subscriber;
    }

    @Override
    public void request(long n) {
        publisher.request(subscriber, n);
    }

    @Override
    public void cancel() {

    }
}
