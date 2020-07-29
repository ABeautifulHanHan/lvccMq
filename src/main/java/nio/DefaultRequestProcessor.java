/**
 * @(#)DefaultRequestProcessor.java, Jul 29, 2020.
 * <p>
 * Copyright 2020 fenbi.com. All rights reserved.
 * FENBI.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package nio;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author lvcc
 */
public class DefaultRequestProcessor implements RequestProcessor {

    private static ExecutorService executorService = Executors.newFixedThreadPool(100);

    @Override
    public void processorRequest(final SelectionKey key, final Server server) {
        executorService.submit(() -> readBuffer(key, server));
    }

    private void readBuffer(SelectionKey key, Server server) {
        try {
            SocketChannel socketChannel = (SocketChannel) key.channel();

            ByteBuffer buffer = ByteBuffer.allocate(1024);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            int len = 0;
            while (true) {
                buffer.clear();
                len = socketChannel.read(buffer);
                if (len == -1) {
                    break;
                }
                buffer.flip();
                while (buffer.hasRemaining()) {
                    baos.write(buffer.get());
                }
            }
            key.attach(baos);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}