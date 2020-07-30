package utils;

import common.Common;
import data.Message;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

/**
 * @author lvcc
 */
public class BufferUtils {

    public static ByteArrayOutputStream readBaosFromBuffer(SocketChannel socketChannel) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ByteBuffer buffer=ByteBuffer.allocate(1024);
        int len;
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
        return baos;
    }

    public static Message readMessage(Object attachmentObject) throws IOException, ClassNotFoundException {
        ByteArrayOutputStream attachment = (ByteArrayOutputStream) attachmentObject;
        Message msg = (Message) SerializeUtils.serializeToObject(new String(attachment.toByteArray(), Common.DEFAULT_CHAR_SETS));
        return msg;
    }
}
