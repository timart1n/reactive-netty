package org.cern.streaming.pool.netty.client.discovery.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.cern.streaming.pool.netty.domain.StreamInfoAnswer;

/**
 * Created by timartin on 10/11/2016.
 */
public class StreamInfoAnswerHandler<T> extends SimpleChannelInboundHandler<StreamInfoAnswer> {

    private final StreamInfoRequestFuture promise;

    public StreamInfoAnswerHandler(StreamInfoRequestFuture promise) {
        this.promise = promise;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, StreamInfoAnswer msg) throws Exception {
        promise.setAnswer(msg);
        ctx.close();
    }
}
