package org.cern.streaming.pool.netty.client.discovery.handler;

import org.cern.streaming.pool.netty.domain.StreamInfoAnswer;

import java.util.Objects;
import java.util.concurrent.*;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;
import java.util.concurrent.locks.Condition;

/**
 * Created by timartin on 10/11/2016.
 */
public class StreamInfoRequestFuture implements Future<StreamInfoAnswer> {

    private StreamInfoAnswer answer;
    private boolean canceled;
    CountDownLatch countDownLatch;

    public StreamInfoRequestFuture() {
        answer = null;
        canceled = false;
        countDownLatch = new CountDownLatch(1);
    }

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        if (answer != null) {
            return false;
        }
        if (canceled) {
            return false;
        }
        canceled = true;
        return true;
    }

    @Override
    public boolean isCancelled() {
        return canceled;
    }

    @Override
    public boolean isDone() {
        if (canceled) {
            return true;
        }
        if (answer != null) {
            return true;
        }
        return false;
    }

    @Override
    public StreamInfoAnswer get() throws InterruptedException, ExecutionException {
        countDownLatch.await();
        return answer;
    }

    @Override
    public StreamInfoAnswer get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        countDownLatch.await(timeout, unit);
        return answer;
    }

    public void setAnswer(StreamInfoAnswer answer) {
        if(!canceled) {
            Objects.requireNonNull(answer);
            this.answer = answer;
            countDownLatch.countDown();
        }
    }
}
