package data;

import lombok.Data;

import java.io.Serializable;
import java.util.HashSet;

/**
 * @author lvcc
 */
@Data
public class Topic implements Serializable {

    private static final long serialVersionUID = 1L;

    //该Topic在Broker中对应的queueId
    private HashSet<Integer> queueId;

    //该Topic对应的cunsumer
    private HashSet<Node> consumer_address;

    private String topicName;//主题名称

    private int queueNum;//请求队列数

    public Topic(String name,int queueNum) {
        topicName = name;
        this.queueNum = queueNum;
        queueId = new HashSet<>();
        consumer_address = new HashSet<>();
    }
}
