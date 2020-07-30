/**
 * @(#)DefaultResponseProcessor.java, Jul 29, 2020.
 * <p>
 * Copyright 2020 fenbi.com. All rights reserved.
 * FENBI.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package nio;

import common.Common;
import data.Message;
import utils.BufferUtils;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

/**
 * @author lvcc
 */
public class DefaultResponseProcessor implements ResponseProcessor {

    @Override
    public void processorResponse(SelectionKey key) {
        Common.executorService.submit(() -> writeBuffer(key));
    }

    // server 回写一个ACK
    private void writeBuffer(SelectionKey key) {
        SocketChannel writeChannel = null;
        try {
            writeChannel = (SocketChannel) key.channel();
            Message msg = BufferUtils.readMessage(key.attachment());
            System.out.println("线程" + Thread.currentThread().getId() + ":服务器收到：" + msg);
            String responseMeg = msg.getId() + "ACK";
            ByteBuffer buffer = ByteBuffer.allocate(1024);
            buffer.put(responseMeg.getBytes(Common.DEFAULT_CHAR_SETS));
            buffer.flip();
            writeChannel.write(buffer);
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                writeChannel.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}