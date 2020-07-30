/**
 * @(#)Consumer.java, Jul 30, 2020.
 * <p>
 * Copyright 2020 fenbi.com. All rights reserved.
 * FENBI.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package consumer;

import common.Common;
import data.Message;
import data.Node;
import data.PullMessage;
import data.RegisterMessage;
import data.Topic;
import nio.Client;
import nio.DefaultRequestProcessor;
import nio.Server;
import org.apache.commons.lang.StringUtils;
import utils.IdUtils;

import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * @author lvcc
 */
public class ConsumerFactory {

    private static ConcurrentHashMap<Node, ConcurrentLinkedQueue<Message>> map = new ConcurrentHashMap<>();

    /**
     * 工厂方法创建consumer
     * @param brokerNode
     * @param consumerNode
     * @throws IOException
     */
    public static void createConsumer(Node brokerNode, Node consumerNode, Topic topic) throws IOException {
        if(map.containsKey(consumerNode)) {
            System.out.println("Node" + consumerNode + "上已存在consumer， 不可重复创建");
            return;
        }
        if (register(brokerNode, consumerNode, topic)) {
            map.put(consumerNode, new ConcurrentLinkedQueue<>());
            waiting(consumerNode);
        } else {
            throw new RuntimeException();
        }
    }

    /**
     * 将consumer注册到broker
     * @param brokerNode
     * @param consumerNode
     */
    private static boolean register(Node brokerNode, Node consumerNode, Topic topic){
        System.out.println("consumer " + consumerNode + "registering...");
        Client client;
        try {
            client = new Client(brokerNode.getIp(), brokerNode.getPort());
            int megId = IdUtils.getId();
            RegisterMessage msg = new RegisterMessage(megId, consumerNode + "register", consumerNode, topic);
            String receivedMsg = client.syscSend(msg);
            if (StringUtils.isNotBlank(receivedMsg) && receivedMsg.contains(String.valueOf(megId)) && receivedMsg.contains("ACK")) {
                System.out.println("consumer " + consumerNode + "register succeed !!!");
                return true;
            } else {
                System.out.println("consumer " + consumerNode + "register failed !!!");
                return false;
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Connection Refuse.");
            return false;
        }
    }

    /**
     * 监听broker传来的消息
     * @param consumerNode
     * @throws IOException
     */
    private static void waiting(Node consumerNode) throws IOException {
        DefaultRequestProcessor defaultRequestProcessor = new DefaultRequestProcessor();
        ConsumerResponseProcessor consumerResponseProcessor = new ConsumerResponseProcessor(consumerNode);
        Common.executorService.submit(() -> {
            System.out.println("Consumer在本地端口" + consumerNode.getPort() + "监听...");
            try {
                new Server(consumerNode.getPort(),defaultRequestProcessor,consumerResponseProcessor);
            } catch (IOException e) {
                System.out.println("端口" + consumerNode.getPort() + "已被占用！");
            }
        });
    }

    /**
     * 拉取消息
     * @param brokerNode
     * @param consumerNode
     */
    public static void pull(Node brokerNode, Node consumerNode) {
        System.out.println("正在拉取消息...");
        Client client;
        try {
            client = new Client(brokerNode.getIp(), brokerNode.getPort());
            PullMessage msg = new PullMessage(IdUtils.getId(), "拉取消息", consumerNode);
            String ack = client.syscSend(msg);
            if (StringUtils.isNotBlank(ack)) {
                Optional<Message> messageOp = getMessage(consumerNode);
                if (messageOp.isPresent()) {
                    System.out.println("消息拉取成功" + messageOp.get().getMessage());
                }
            }
        } catch (IOException e) {
            System.out.println("Connection Refuse.");
        }
    }

    /**
     * 从队列中读取消息消费
     * @param node
     * @return
     */
    public static Optional<Message> getMessage(Node node) {
        if (ConsumerFactory.getList(node).isEmpty()) {
            return Optional.empty();
        }
        return Optional.ofNullable(ConsumerFactory.getList(node).poll());
    }

    /**
     * 拿到consumer的消费队列
     * @param node
     * @return
     */
    public static ConcurrentLinkedQueue<Message> getList(Node node){
        return map.get(node);
    }
}