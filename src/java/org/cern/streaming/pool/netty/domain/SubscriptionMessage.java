package org.cern.streaming.pool.netty.domain;

import cern.streaming.pool.core.service.StreamId;

/**
 * Created by timartin on 09/11/2016.
 */
public class SubscriptionMessage<T> extends StatusMessage {

    private final StreamId<T> streamId;

    public SubscriptionMessage(StreamId<T> streamId) {
        super(10, "Subscription request.");
        this.streamId = streamId;
    }

    public StreamId<T> getStreamId() {
        return streamId;
    }
}
