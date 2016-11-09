package org.cern.streaming.pool.netty.domain;

/**
 * {@link StatusMessage} used to request items to be emitted by the reactive publisher.
 */
public class RequestMessage extends StatusMessage {

    private final long numItems;

    public RequestMessage(long numItems) {
        super(3, "Streaming request");
        this.numItems = numItems;

    }

    public long getNumItems() {
        return numItems;
    }
}
