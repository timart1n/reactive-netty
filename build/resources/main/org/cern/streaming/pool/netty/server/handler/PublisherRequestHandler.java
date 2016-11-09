package org.cern.streaming.pool.netty.server.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.cern.streaming.pool.netty.domain.RequestMessage;
import org.cern.streaming.pool.netty.server.NettyPublisher;

/**
 * Created by timartin on 09/11/2016.
 */
public class PublisherRequestHandler extends SimpleChannelInboundHandler<RequestMessage> {

    private final NettyPublisher<?> nettyPublisher;

    public PublisherRequestHandler(NettyPublisher<?> nettyPublisher) {
        this.nettyPublisher = nettyPublisher;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RequestMessage msg) throws Exception {
        nettyPublisher.request(msg.getNumItems(), ctx.channel());
    }
}
