package data;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

/**
 * @author lvcc
 */
@Data
@AllArgsConstructor
public class Message implements Serializable {

    private static final long serialVersionUID = 1L;

    private int id;                                    //消息序号

    private String message;                             //消息

    private MessageType messageType;                    //消息类型

    private Topic topic;                                // topic
}
