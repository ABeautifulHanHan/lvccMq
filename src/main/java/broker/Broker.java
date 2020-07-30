package broker;

import data.Message;
import data.Node;
import data.Topic;
import nio.Client;
import nio.DefaultRequestProcessor;
import nio.Server;
import org.apache.commons.lang.StringUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * @author lvcc
 */
public class Broker {

    /**
     * 记录队列编号
     */
    private volatile int count = 0;

    /**
     * push时间默认一秒一次
     */
    private int push_Time = 1000;

    /**
     * sync时间默认一秒一次
     */
    private int sync_Time = 1000;

    /**
     * 发送失败重试次数
     */
    private int reTry_Time = 16;

    /**
     * consumer 列表
     */
    private List<Node> consumers;

    private ConcurrentHashMap<String, List<Node>> topic2Consumers;

    private ConcurrentHashMap<String, MessageQueue> queueList;

    /**
     * consumer 对应的nio client
     */
    private Map<Node, Client> clients;

    public Broker(int port) throws IOException {
        init(port, 10);
    }

    public Broker(int port, int queueNum) throws IOException {
        init(port, queueNum);
    }

    /**
     * 启动broker
     * @param port
     * @param queueNum
     * @throws IOException
     */
    private void init(int port, int queueNum) throws IOException {
        System.out.println("Broker已启动，在本地" + port + "端口监听。");
        // 初始化消费者
        consumers = new ArrayList<>();
        // 创建客户端集合
        clients = new HashMap<>();
        //创建队列库
        queueList = new ConcurrentHashMap<>(queueNum);

        topic2Consumers = new ConcurrentHashMap<>();

        createQueue(10);

        //监听生产者
        DefaultRequestProcessor defaultRequestProcessor = new DefaultRequestProcessor();
        BrokerResponseProcessor brokerResponseProcessor = new BrokerResponseProcessor(this);
        new Thread(() -> {
            try {
                new Server(port, defaultRequestProcessor, brokerResponseProcessor);
            } catch (IOException e) {
                System.out.println("broker 启动失败！！！");
                e.printStackTrace();
            }
        }).start();
    }

    /**
     * 创建queueNum个队列，默认是10
     * @param queueNum
     */
    private synchronized void createQueue(int queueNum) {
        for(int i=1; i<=queueNum; i++) {
            MessageQueue queue = new MessageQueue();
            queueList.put(String.valueOf(count++), queue);
        }
    }

    public synchronized void addMessage(int queueNumber, Message message) {
        MessageQueue queue = queueList.get(String.valueOf(queueNumber));
        queue.putAtHeader(message);
    }

    public void addMessageToBroker(Message msg) {
        List<Integer> list = msg.getTopic().getQueueIds();
        Topic topic = msg.getTopic();
        topic.addConsumers(topic2Consumers.get(topic.getTopicName()));
        for(Integer i: list) {
            addMessage(i, msg);
        }
    }

    /**
     * 添加消费者
     * @param node
     */
    public void addConsumer(Node node, Topic topic) {
        consumers.add(node);
        Client client;
        try {
            client = new Client(node.getIp(), node.getPort());
            clients.put(node, client);
            if (!topic2Consumers.contains(topic.getTopicName())) {
                topic2Consumers.put(topic.getTopicName(), new ArrayList<>());
            }
            topic2Consumers.get(topic.getTopicName()).add(node);
        } catch (IOException e) {
            System.out.println("Connection Refuse.");
        }

    }

    /**
     * 为producer分配队列
     * @param queueNum
     * @return
     */
    public List<Integer> choiceQueue(int queueNum) {
        if (queueNum > queueList.size()) {
            createQueue(queueNum - queueList.size());
        }
        List<Integer> results = new ArrayList<>();
        for (int i=0; i<Math.min(queueNum, queueList.size()); i++) {
            results.add(i);
        }
        return results;
    }

    /**
     * 响应consumer拉取消息
     * @param consumerNode
     */
    public void pullMessage(Node consumerNode) {
        List<Message> list = new ArrayList<>();
        //查找队列最外层消息，找到对应ipNode的消息
        for (MessageQueue queue:queueList.values()) {
            if (queue.getTail() != null) {
                List<Node> consumers = queue.getTail().getTopic().getConsumers();
                //消息消费者只有一个，该消息出队, 消息消费者不止一个，则删除这个消费者，并将该消息推送给它
                if (consumers.contains(consumerNode) && consumers.size()==1) {
                    list.add(queue.getAndRemoveTail());
                }  else if(consumers.contains(consumerNode) && consumers.size()>1) {
                    Message m = queue.getTail();
                    m.getTopic().deleteConsumer(consumerNode);
                    list.add(m);
                }
            }
        }
        for(Message message:list) {
            try {
                Client client = new Client(consumerNode.getIp(), consumerNode.getPort());
                // 失败重试
                for (int i=0; i < reTry_Time; i++) {
                    String ack = null;
                    try {
                        System.out.println("向消费者" + consumerNode + "push消息" + message);
                        ack = client.syscSend(message);
                    } catch (IOException e) {
                        System.out.println("发送失败！正在重试第" + (i+1) + "次...");
                    }
                    if (StringUtils.isNotBlank(ack)) {
                        break;
                    }
                }
            } catch (IOException e1) {
                System.out.println("消费者不存在");
            }
        }
    }
}
