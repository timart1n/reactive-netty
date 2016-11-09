package org.cern.streaming.pool.netty.domain;

/**
 *
 */
public class OnSubscribeMessage extends StatusMessage {
    public OnSubscribeMessage(int statusCode, String statusMessage) {
        super(statusCode, statusMessage);
    }


}
