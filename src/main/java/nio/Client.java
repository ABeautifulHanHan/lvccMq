/**
 * @(#)Client.java, Jul 29, 2020.
 * <p>
 * Copyright 2020 fenbi.com. All rights reserved.
 * FENBI.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package nio;

import utils.BufferUtils;
import utils.SerializeUtils;
import data.Message;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

import static common.Common.DEFAULT_CHAR_SETS;

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

    private void init() throws IOException {
        //1.创建SocketChannel
        socketChannel = SocketChannel.open();
        //2.连接服务器
        socketChannel.connect(new InetSocketAddress(ip,port));
    }

    // 同步发送消息, 并接收服务端消息
    public String syscSend(Message msg) throws IOException{
        init();
        String string = SerializeUtils.serialize(msg);
        //写数据
        ByteBuffer buffer=ByteBuffer.allocate(1024 * 1024);
        buffer.clear();
        buffer.put(string.getBytes(DEFAULT_CHAR_SETS));
        buffer.flip();
        socketChannel.write(buffer);
        socketChannel.shutdownOutput();
        return receive();
    }

    public String receive() {
        try {
            return new String(BufferUtils.readBaosFromBuffer(socketChannel).toByteArray());
        }catch(IOException e) {
            System.out.println("Connection Refuse.");
        }
        return null;
    }
}