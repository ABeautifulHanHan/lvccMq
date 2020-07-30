/**
 * @(#)RegisterMessage.java, Jul 30, 2020.
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
public class RegisterMessage extends Message {

    public Node node;

    protected static MessageType messageType = MessageType.REGISTER_TOPIC;

    public RegisterMessage(int id, String message, Node node, Topic topic) {
        super(id, message, messageType);
        this.node = node;
        this.topic = topic;
    }
}