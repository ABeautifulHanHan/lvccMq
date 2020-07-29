/**
 * @(#)Client.java, Jul 29, 2020.
 * <p>
 * Copyright 2020 fenbi.com. All rights reserved.
 * FENBI.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

/**
 * @author lvcc
 */
public class Client {

    private SocketChannel socketChannel = null;

    private volatile static int count;

    private String ip;

    private int port;

    public Client(String ip,int port) throws IOException {
        this.ip = ip;
        this.port = port;
    }

    private void init(String ip, int port) throws IOException {
        //1.创建SocketChannel
        socketChannel = SocketChannel.open();
        //2.连接服务器
        socketChannel.connect(new InetSocketAddress(ip,port));
    }


}