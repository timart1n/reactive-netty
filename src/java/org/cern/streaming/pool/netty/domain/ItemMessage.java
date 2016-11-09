package org.cern.streaming.pool.netty.domain;

import java.io.Serializable;

/**
 * Created by timartin on 25/10/2016.
 */
public class ItemMessage<T> extends StatusMessage {

    private final T item;

    public ItemMessage(T item) {
        super(4, "Emitted item");
        this.item = item;
    }

    public T getItem() {
        return item;
    }

    @Override
    public String toString() {
        return "ItemMessage{" +
                "item=" + item.toString() +
                '}';
    }
}
