package data;

import lombok.Data;

import java.io.Serializable;

/**
 * @author lvcc
 */
@Data
public class Message implements Serializable {

    private static final long serialVersionUID = 1L;

    //消息序号
    public int id;

    //消息
    public String message;

    //消息类型
    public MessageType messageType;

    // topic
    public Topic topic;

    public Message(int id, String message, MessageType messageType) {
        this.id = id;
        this.message = message;
        this.messageType = messageType;
    }

    public Message(int id, String message, MessageType messageType, Topic topic) {
        this.id = id;
        this.message = message;
        this.messageType = messageType;
        this.topic = topic;
    }
}
