package org.cern.streaming.pool.netty.domain;

import cern.streaming.pool.core.service.StreamId;

import java.io.Serializable;

/**
 * This class is used to manage the communication between the clent and server nodes.
 *
 * @author timartin
 */
public class StatusMessage implements Serializable {

    private final StatusCode statusCode;
    private final String statusMessage;

    public StatusMessage(StatusCode statusCode, String statusMessage) {
        this.statusCode = statusCode;
        this.statusMessage = statusMessage;
    }

    public StatusCode getStatusCode() {
        return statusCode;
    }

    public String getStatusMessage() {
        return statusMessage;
    }

    public enum StatusCode {
        OK,
        ERROR
    }
}
