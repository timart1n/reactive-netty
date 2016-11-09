package org.cern.streaming.pool.netty;

import io.netty.channel.ChannelFuture;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import org.cern.streaming.pool.netty.client.NettyPublisherProxy;
import org.cern.streaming.pool.netty.domain.StreamIdSample;
import org.cern.streaming.pool.netty.server.NettyPublisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

/**
 * Created by timartin on 21/10/2016.
 */
public class Main {
    public static void main(String[] args) throws InterruptedException {

        NettyPublisher<Long> publisher = new NettyPublisher<Long>("192.168.99.1", 9999, 5) {
            @Override
            public void subscribe(Subscriber s) {
                new Thread(() -> {
                    try {
                        Thread.sleep(1000);
                        System.out.println("Value sent");
                        s.onNext(new Long(2));
                        s.onNext(new Long(2));
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }).start();
            }
        };
        ChannelFuture channelFuture = publisher.bindChannel();
        channelFuture.addListener(new GenericFutureListener<Future<? super Void>>() {
            @Override
            public void operationComplete(Future<? super Void> future) throws Exception {
                if (future.isSuccess()) {
                    connect();
                    connect();
                } else {
                    future.cause().printStackTrace();
                }
            }
        });

        channelFuture.sync();
    }

    public static void connect() {
        NettyPublisherProxy<Long> publisherProxy = new NettyPublisherProxy<>(9999, "192.168.99.1", 5, new StreamIdSample());
        publisherProxy.subscribe(new Subscriber<Long>() {
            @Override
            public void onSubscribe(Subscription s) {
                System.out.println("onSubscribe");
                s.request(1);
            }

            @Override
            public void onNext(Long aLong) {
                System.out.println("onNext");
            }

            @Override
            public void onError(Throwable t) {
                System.out.println("onError");
            }

            @Override
            public void onComplete() {
                System.out.println("onComplete");
            }
        });
    }
}
