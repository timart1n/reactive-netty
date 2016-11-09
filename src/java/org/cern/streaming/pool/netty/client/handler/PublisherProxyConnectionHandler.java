package org.cern.streaming.pool.netty.client.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.cern.streaming.pool.netty.client.NettyPublisherProxy;
import org.cern.streaming.pool.netty.domain.StatusMessage;

/**
 * Created by timartin on 08/11/2016.
 */
public class PublisherProxyConnectionHandler extends SimpleChannelInboundHandler<StatusMessage> {

    private final NettyPublisherProxy<?> publisherProxy;

    public PublisherProxyConnectionHandler(NettyPublisherProxy<?> publisherProxy) {
        this.publisherProxy = publisherProxy;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, StatusMessage msg) throws Exception {
        publisherProxy.handleSubscriptionAnswer(msg);
    }
}
