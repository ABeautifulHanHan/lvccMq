/**
 * @(#)PullMessage.java, Jul 30, 2020.
 * <p>
 * Copyright 2020 fenbi.com. All rights reserved.
 * FENBI.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package data;

import lombok.Data;

/**
 * @author lvcc
 */
@Data
public class PullMessage extends Message {

    private Node node;

    public PullMessage(int id, String message, Node node) {
        super(id, message, MessageType.PULL_MESSAGE);
        this.node = node;
    }
}