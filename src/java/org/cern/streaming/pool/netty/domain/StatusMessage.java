package org.cern.streaming.pool.netty.domain;

import cern.streaming.pool.core.service.StreamId;

import java.io.Serializable;

/**
 * This class is used to manage the communication between the clent and server nodes.
 *
 * @author timartin
 */
public class StatusMessage implements Serializable {

    private final int statusCode;
    private final String statusMessage;

    public StatusMessage(int statusCode, String statusMessage) {
        this.statusCode = statusCode;
        this.statusMessage = statusMessage;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getStatusMessage() {
        return statusMessage;
    }

    public boolean isError() {
        return statusCode == 2;
    }

    public static final StatusMessage ACCEPTED() {
        return new StatusMessage(0, "Request accepted.");
    }

    public static final StatusMessage ERROR(String errorMessage) {
        return new StatusMessage(2, errorMessage);
    }

    public static final StatusMessage COMPLETE() {
        return new StatusMessage(1, "Stream completed.");
    }
}
