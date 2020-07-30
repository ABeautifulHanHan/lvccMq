package broker;

import common.Common;
import data.Message;
import data.MessageType;
import data.PullMessage;
import data.RegisterMessage;
import nio.ResponseProcessor;
import utils.BufferUtils;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.List;

/**
 * @author lvcc
 */
public class BrokerResponseProcessor implements ResponseProcessor {

    private Broker broker;

    public BrokerResponseProcessor(Broker broker) {
        this.broker = broker;
    }

    @Override
    public void processorResponse(SelectionKey key) {
        //拿到线程并执行
        Common.executorService.submit(() -> {
            SocketChannel writeChannel = null;
            try {
                // 写操作
                writeChannel = (SocketChannel) key.channel();
                //拿到客户端传递的数据
                Message msg = BufferUtils.readMessage(key.attachment());
                ByteBuffer buffer = ByteBuffer.allocate(1024);
                // 注册consumer
                if (msg.getMessageType() == MessageType.REGISTER_TOPIC) {
                    handlerRegisterMessage(writeChannel, buffer, msg);
                } else if (msg.getMessageType() == MessageType.PULL_MESSAGE) {
                    handlerPullMessage(writeChannel, buffer, msg);
                } else if (msg.getMessageType() == MessageType.REQUEST_QUEUE) {
                    handleRequestQueue(writeChannel, buffer, msg);
                } else if(msg.getMessageType() == MessageType.REPLY_EXPECTED) {
                    handleReplyQueue(writeChannel, buffer, msg);
                }
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }finally {
                try {
                    assert writeChannel != null;
                    writeChannel.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void handleReplyQueue(SocketChannel writeChannel, ByteBuffer buffer, Message msg) throws IOException  {
        System.out.println("收到生产者发来的消息" + msg);
        broker.addMessageToBroker(msg);
        String message = msg.getId() + " ACK";
        buffer.put(message.getBytes(Common.DEFAULT_CHAR_SETS));
        buffer.flip();
        writeChannel.write(buffer);
    }

    private void handleRequestQueue(SocketChannel writeChannel, ByteBuffer buffer, Message msg) throws IOException  {
        Integer queueNum = Integer.valueOf(msg.getTopic().getQueueNum());
        List<Integer> queueIds = broker.choiceQueue(queueNum);
        String message = "";
        for (Integer i: queueIds) {
            message += i + " ";
        }
        message += "ACK";
        buffer.put(message.getBytes(Common.DEFAULT_CHAR_SETS));
        buffer.flip();
        writeChannel.write(buffer);
    }

    private void handlerPullMessage(SocketChannel writeChannel, ByteBuffer buffer, Message msg) throws IOException  {
        PullMessage pullMessage = (PullMessage) msg;
        broker.pullMessage(pullMessage.getNode());
        String message = msg.getId() + " ACK";
        buffer.put(message.getBytes(Common.DEFAULT_CHAR_SETS));
        buffer.flip();
        writeChannel.write(buffer);
    }

    private void handlerRegisterMessage(SocketChannel writeChannel, ByteBuffer buffer, Message msg) throws IOException {
        RegisterMessage registerMessage = (RegisterMessage) msg;
        System.out.println("broker 收到consumer注册消息：" + registerMessage);
        broker.addConsumer(registerMessage.getNode(), registerMessage.getTopic());
        String message = msg.getId() + " ACK";
        buffer.put(message.getBytes(Common.DEFAULT_CHAR_SETS));
        buffer.flip();
        writeChannel.write(buffer);
    }
}
