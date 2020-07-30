package data;

import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * @author lvcc
 */
@Data
public class Topic implements Serializable {

    private static final long serialVersionUID = 1L;

    //该Topic在Broker中对应的queueId
    private HashSet<Integer> queueIds;

    //该Topic对应的cunsumer
    private HashSet<Node> consumerAddress;

    private String topicName;//主题名称

    // 队列数，默认为10
    private int queueNum = 10;

    public Topic(String s, int queueNum) {
        topicName = s;
        this.queueNum = queueNum;
        queueIds = new HashSet<>();
        consumerAddress = new HashSet<>();
    }
    public Topic(String s, HashSet<Integer> queueId, HashSet<Node> consumerAddress) {
        topicName = s;
        this.queueIds = queueId;
        this.consumerAddress = consumerAddress;
    }

    public void addConsumer(Node consumer) {
        consumerAddress.add(consumer);
    }

    public void addConsumers(List<Node> consumers) {
        consumerAddress.addAll(consumers);
    }

    public void addQueueId(int queueId) {
        queueIds.add(queueId);
    }

    public void deleteConsumer(Node consumer) {
        consumerAddress.remove(consumer);
    }

    public List<Node> getConsumers() {
        return new ArrayList<>(consumerAddress);
    }

    public List<Integer> getQueueIds() {
        return new ArrayList<>(queueIds);
    }
}
