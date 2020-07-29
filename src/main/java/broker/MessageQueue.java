package broker;

import data.Message;

import java.io.Serializable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedDeque;

/**
 * @author lvcc
 */
public class MessageQueue implements Serializable {

    private static final long serialVersionUID = 1L;

    private volatile static int count = 1;//测试

    private ConcurrentLinkedDeque<Message> queue;

    public MessageQueue() {
        queue = new ConcurrentLinkedDeque<>();
    }

    public void putAtHeader(Message value) {
        queue.addFirst(value);
    }

    public Message getAndRemoveTail() {
        return queue.pollLast();
    }

    public Message getTail() {
        return queue.peekLast();
    }

    public int size() {
        return queue.size();
    }

    public void outputAll() {
        Iterator<Message> iterator = queue.iterator();
        while(iterator.hasNext()){
            System.out.print(iterator.next().getMessage()+" ");
        }
        System.out.println();
    }

    //逆序输出
    public List<Message> getReverseAll() {
        Iterator<Message> iterator = queue.iterator();
        LinkedList<Message> list = new LinkedList<Message>();
        while(iterator.hasNext()){
            list.addFirst(iterator.next());
        }
        return list;
    }
}
