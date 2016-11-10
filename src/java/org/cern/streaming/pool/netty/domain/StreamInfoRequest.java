package org.cern.streaming.pool.netty.domain;

import cern.streaming.pool.core.service.StreamId;

import java.io.Serializable;

/**
 * Domain class used to request the information of where a remote stream is being exposed.
 */
public class StreamInfoRequest<T> implements Serializable {

    /**
     * The {@link StreamId} that identifies the remote stream on the discovery service.
     */
    private final StreamId<T> streamId;

    /**
     * Default constructor.
     */
    public StreamInfoRequest(StreamId<T> streamId) {
        this.streamId = streamId;
    }

    public StreamId<T> getStreamId() {
        return streamId;
    }
}
