/**
 * @(#)DefaultResponseProcessor.java, Jul 29, 2020.
 * <p>
 * Copyright 2020 fenbi.com. All rights reserved.
 * FENBI.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package nio;

import common.Config;
import data.Message;
import utils.SerializeUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author lvcc
 */
public class DefaultResponseProcessor implements ResponseProcessor {

    private static ExecutorService executorService = Executors.newFixedThreadPool(3);

    @Override
    public void processorResponse(SelectionKey key) {
        executorService.submit(() -> writeBuffer(key));
    }

    // server 回写一个ACK
    private void writeBuffer(SelectionKey key) {
        SocketChannel writeChannel = null;
        try {
            writeChannel = (SocketChannel) key.channel();
            ByteArrayOutputStream attachment = (ByteArrayOutputStream) key.attachment();
            String receivedMsg = new String(attachment.toByteArray(), Config.DEFAULT_CHAR_SETS);
            Message msg = (Message) SerializeUtils.serializeToObject(receivedMsg);
            System.out.println("线程" + Thread.currentThread().getId() + ":服务器收到：" + msg);
            String responseMeg = msg.getId() + "ACK";
            ByteBuffer buffer = ByteBuffer.allocate(1024);
            buffer.put(responseMeg.getBytes(Config.DEFAULT_CHAR_SETS));
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