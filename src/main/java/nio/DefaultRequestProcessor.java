/**
 * @(#)DefaultRequestProcessor.java, Jul 29, 2020.
 * <p>
 * Copyright 2020 fenbi.com. All rights reserved.
 * FENBI.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package nio;

import utils.BufferUtils;

import java.io.ByteArrayOutputStream;
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
            ByteArrayOutputStream baos = BufferUtils.readBaosFromBuffer(socketChannel);
            key.attach(baos);
            server.addWriteQueen(key);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}