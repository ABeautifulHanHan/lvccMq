package broker;

import data.Node;
import nio.Client;
import nio.DefaultRequestProcessor;
import nio.Server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author lvcc
 */
public class Broker {

    private volatile int count = 0;              //记录队列编号

    private int push_Time = 1000;                //push时间默认一秒一次

    private int sync_Time = 1000;               //sync时间默认一秒一次

    private int reTry_Time = 16;                //发送失败重试次数

    private List<Node> consumers;

    // 消费者对应的client
    private Map<Node, Client> clients;

    private ConcurrentHashMap<String, MessageQueue> queueList;//队列列表

    public Broker(int port) throws IOException {
        init(port, 10);
    }

    public Broker(int port, int queueNum) throws IOException {
        init(port, queueNum);
    }

    // 启动broker
    private void init(int port, int queueNum) throws IOException {
        System.out.println("Broker已启动，在本地" + port + "端口监听。");
        // 初始化消费者
        consumers = new ArrayList<>();
        // 创建客户端集合
        clients = new HashMap<>();
        //创建队列库
        queueList = new ConcurrentHashMap<>();
        //默认创建十个队列

        //监听生产者
        DefaultRequestProcessor defaultRequestProcessor = new DefaultRequestProcessor();
        BrokerResponseProcessor brokerResponeProcessor = new BrokerResponseProcessor();
        Broker broker = this;
        new Thread() {
            public void run() {
                try {
                    new Server(port, defaultRequestProcessor, brokerResponeProcessor);
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }

            ;
        }.start();
    }

    //创建队列
    private synchronized void createQueue(int queueNum) {
        for(int i=1; i<=queueNum; i++) {
            MessageQueue queue = new MessageQueue();
            queueList.put((count++)+"", queue);
        }
    }
}
