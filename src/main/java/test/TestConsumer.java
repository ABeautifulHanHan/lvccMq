/**
 * @(#)TestConsumer.java, Jul 30, 2020.
 * <p>
 * Copyright 2020 fenbi.com. All rights reserved.
 * FENBI.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package test;

import common.Common;
import consumer.ConsumerFactory;
import data.Topic;

/**
 * @author lvcc
 */
public class TestConsumer {

    public static void main(String[] args) {
        Topic topic = new Topic("lvcc", 1);
        try {
            ConsumerFactory.createConsumer(Common.BROKER_NODE, Common.CONSUMER_NODE, topic);
            while (true) {
                ConsumerFactory.pull(Common.BROKER_NODE, Common.CONSUMER_NODE);
                Thread.sleep(10000);
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(0);
        }
    }
}