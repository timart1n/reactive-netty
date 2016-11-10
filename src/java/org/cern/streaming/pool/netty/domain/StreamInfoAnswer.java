package org.cern.streaming.pool.netty.domain;

import java.io.Serializable;

/**
 * Domain class used to transmit the information of where a remote stream is being exposed.
 */
public final class StreamInfoAnswer implements Serializable {

    /**
     * An enum that represents the result of the request.
     */
    private final StatusCode statusCode;

    /**
     * The host where the remote stream is being exposed.
     */
    private final String host;

    /**
     * The port where the remote stream is being exposed.
     */
    private final int port;

    /**
     * Default constructor.
     */
    public StreamInfoAnswer(StatusCode statusCode, String host, int port) {
        this.statusCode = statusCode;
        this.host = host;
        this.port = port;
    }

    public StatusCode getStatusCode() {
        return statusCode;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }
}
