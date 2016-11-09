package org.cern.streaming.pool.netty.client.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.cern.streaming.pool.netty.client.NettyPublisherProxy;
import org.cern.streaming.pool.netty.domain.ItemMessage;

/**
 * Created by timartin on 09/11/2016.
 */
public class PublisherProxyItemHandler<T> extends SimpleChannelInboundHandler<ItemMessage<T>> {

    private final NettyPublisherProxy<T> publisherProxy;

    public PublisherProxyItemHandler(NettyPublisherProxy<T> publisherProxy) {
        this.publisherProxy = publisherProxy;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ItemMessage<T> msg) throws Exception {
        System.out.println("Got a value in channel " + ctx.channel());
        publisherProxy.handleItem(msg);
    }
}
