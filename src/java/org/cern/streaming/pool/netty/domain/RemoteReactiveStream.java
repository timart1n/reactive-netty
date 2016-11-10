package org.cern.streaming.pool.netty.domain;

import cern.streaming.pool.core.service.ReactiveStream;
import io.netty.util.concurrent.Promise;
import org.cern.streaming.pool.netty.client.NettyPublisherProxy;
import org.reactivestreams.Publisher;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;


/**
 *
 */
public class RemoteReactiveStream<T> implements ReactiveStream<T> {

    private NettyPublisherProxy<T> proxy;
    private final Future<StreamInfoAnswer> promise;

    public RemoteReactiveStream(Future<StreamInfoAnswer> promise) {
        this.promise = promise;
    }

    public Publisher<T> getSource() throws ExecutionException, InterruptedException {
        if (proxy == null) {
            StreamInfoAnswer streamInfoAnswer = promise.get();
            proxy = new NettyPublisherProxy<T>(streamInfoAnswer.getPort(), streamInfoAnswer.getHost(), 5);
        }
        return proxy;
    }
}
