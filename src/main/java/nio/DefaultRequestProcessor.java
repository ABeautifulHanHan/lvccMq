/**
 * @(#)DefaultRequestProcessor.java, Jul 29, 2020.
 * <p>
 * Copyright 2020 fenbi.com. All rights reserved.
 * FENBI.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package nio;

import common.Common;
import utils.BufferUtils;

import java.io.ByteArrayOutputStream;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

/**
 * @author lvcc
 */
public class DefaultRequestProcessor implements RequestProcessor {

    @Override
    public void processorRequest(final SelectionKey key, final Server server) {
        Common.executorService.submit(() -> readBuffer(key, server));
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