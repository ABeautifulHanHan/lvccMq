/**
 * @(#)SyscProducerFactory.java, Jul 30, 2020.
 * <p>
 * Copyright 2020 fenbi.com. All rights reserved.
 * FENBI.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package producer;

import data.Message;
import data.MessageType;
import data.Node;
import data.Topic;
import nio.Client;
import org.apache.commons.lang.StringUtils;
import utils.IdUtils;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author lvcc
 */
public class SyscProducerFactory {

    /**
     * 该生产者是否已申请队列
     */
    private static ConcurrentHashMap<Node, Boolean> requestMap= new ConcurrentHashMap<>();
    /**
     * 重试次数
     */
    private static int reTry_Time = 16;

    public static void setReTry_Time(int reTry_Time) {
        SyscProducerFactory.reTry_Time = reTry_Time;
    }


    public static String send(Message msg, String ip, int port) {
        Node ipNode = new Node(ip, port);
        if(requestMap.get(ipNode) == null) {
            System.out.println("未向Broker申请队列！");
            return null;
        }
        Client client;
        if (msg.getMessageType() != MessageType.REPLY_EXPECTED && msg.getMessageType() != MessageType.REQUEST_QUEUE) {
            msg.setMessageType(MessageType.REPLY_EXPECTED);
        }
        for(int i=0; i<reTry_Time; i++) {
            try {
                client = new Client(ip, port);
                String result = client.syscSend(msg);
                if (result != null) {
                    System.out.println("向broker发送消息" + msg + "成功");
                    return result;
                }
                if (result.equals("")) {
                    return null;
                }
            } catch (IOException e) {
                System.out.println("生产者消息发送失败，正在重试第" + (i+1) + "次...");
            }
        }
        return null;
    }

    /**
     * 申请队列, 未成功返回null
     * @param msg
     * @param ip
     * @param port
     * @return
     */
    private static String sendQueueRegister(Message msg, String ip, int port) {
        Client client;
        if (msg.getMessageType()!= MessageType.REPLY_EXPECTED && msg.getMessageType() != MessageType.REQUEST_QUEUE) {
            msg.setMessageType(MessageType.REPLY_EXPECTED);
        }
        try {
            client = new Client(ip, port);
            for(int i=0; i<reTry_Time; i++) {
                String result = client.syscSend(msg);
                if (StringUtils.isNotBlank(result)) {
                    System.out.println("队列申请成功！");
                    return result;
                }
                if ("".equals(result)) {
                    return null;
                }
            }
        } catch (IOException e) {
            System.out.println("Broker未上线！");
        }
        return null;
    }

    /**
     * 向Broker申请队列，topic包含队列个数
     * @param topic
     * @param ip
     * @param port
     * @return
     */
    public static Topic requestQueue(Topic topic, String ip, int port){
        System.out.println("请求向Broker申请队列...");
        Message message = new Message(IdUtils.getId(), "RequestQueue", MessageType.REQUEST_QUEUE, topic);
        String queueIds = SyscProducerFactory.sendQueueRegister(message, ip, port);
        if (StringUtils.isBlank(queueIds)) {
            System.out.println("申请队列失败！");
            return topic;
        }
        System.out.println(queueIds);
        String[] queueIdsArray = queueIds.split(" ");
        for (int i=0; i<queueIdsArray.length-1; i++) {
            topic.addQueueId(Integer.parseInt(queueIdsArray[i]));
        }
        System.out.println(topic);
        Node ipNode = new Node(ip, port);
        requestMap.put(ipNode, true);
        return topic;
    }
}