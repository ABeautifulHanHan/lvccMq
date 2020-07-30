/**
 * @(#)TestProducer.java, Jul 30, 2020.
 * <p>
 * Copyright 2020 fenbi.com. All rights reserved.
 * FENBI.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package test;

import common.Common;
import data.Message;
import data.MessageType;
import data.Topic;
import producer.SyscProducerFactory;
import utils.IdUtils;

/**
 * @author lvcc
 */
public class TestProducer {

    public static void main(String[] args) {
        Topic topic = new Topic("lvcc", 1);
        Message message = new Message(IdUtils.getId(), "lvcc的消息", MessageType.REPLY_EXPECTED, topic);
        Topic registerTopic = SyscProducerFactory.requestQueue(topic, Common.BROKER_NODE.getIp(), Common.BROKER_NODE.getPort());
        message.setTopic(registerTopic);
        SyscProducerFactory.send(message, Common.BROKER_NODE.getIp(), Common.BROKER_NODE.getPort());
    }
}