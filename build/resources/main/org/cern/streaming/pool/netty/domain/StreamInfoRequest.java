package org.cern.streaming.pool.netty.domain;

import cern.streaming.pool.core.service.StreamId;

import java.io.Serializable;

/**
 * Created by timartin on 10/11/2016.
 */
public class StreamInfoRequest<T> implements Serializable {

    private final StreamId<T> streamId;

    public StreamInfoRequest(StreamId<T> streamId) {
        this.streamId = streamId;
    }

    public StreamId<T> getStreamId() {
        return streamId;
    }
}
