/**
 * @(#)TestBroker.java, Jul 30, 2020.
 * <p>
 * Copyright 2020 fenbi.com. All rights reserved.
 * FENBI.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package test;

import broker.Broker;
import common.Common;

/**
 * @author lvcc
 */
public class TestBroker {

    public static void main(String[] args) {
        try {
            Broker broker = new Broker(Common.BROKER_NODE.getPort());
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(0);
        }
    }
}