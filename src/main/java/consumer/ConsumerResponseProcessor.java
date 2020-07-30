/**
 * @(#)ConsumerResponseProcessor.java, Jul 30, 2020.
 * <p>
 * Copyright 2020 fenbi.com. All rights reserved.
 * FENBI.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package consumer;

import common.Common;
import data.Message;
import data.MessageType;
import data.Node;
import nio.ResponseProcessor;
import utils.BufferUtils;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * @author lvcc
 */
public class ConsumerResponseProcessor implements ResponseProcessor {

    private Node node;

    public ConsumerResponseProcessor(Node node) {
        this.node = node;
    }

    @Override
    public void processorResponse(SelectionKey key) {
        Common.executorService.submit(() -> {
            SocketChannel writeChannel = null;
            try {
                writeChannel = (SocketChannel) key.channel();
                Message msg = BufferUtils.readMessage(key.attachment());
                ConcurrentLinkedQueue<Message> list = ConsumerFactory.getList(node);
                list.add(msg);

                ByteBuffer buffer = ByteBuffer.allocate(1024);
                if(msg.getMessageType() == MessageType.REPLY_EXPECTED) {
                    String message = msg.getId() + " ACK";
                    buffer.put(message.getBytes(Common.DEFAULT_CHAR_SETS));
                    buffer.flip();
                    writeChannel.write(buffer);
                }
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }finally {
                try {
                    writeChannel.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}