/**
 * @(#)DefaultResponseProcessor.java, Jul 29, 2020.
 * <p>
 * Copyright 2020 fenbi.com. All rights reserved.
 * FENBI.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package nio;

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

    private static ExecutorService executorService = Executors.newFixedThreadPool(100);

    @Override
    public void processorResponse(SelectionKey key) {
        executorService.submit(() -> writeBuffer(key));
    }

    // server 回写一个ACK
    private void writeBuffer(SelectionKey key) {
        SocketChannel writeChannel = null;
        try {
            writeChannel = (SocketChannel) key.channel();
            ByteArrayOutputStream attachment = (ByteArrayOutputStream)key.attachment();
            ByteBuffer buffer = ByteBuffer.allocate(1024);
            System.out.println("服务器收到：" + new String(attachment.toByteArray()));
            String message = new String(attachment.toByteArray()) + "ACK";
            buffer.put(message.getBytes());
            buffer.flip();
            writeChannel.write(buffer);

        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                writeChannel.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}