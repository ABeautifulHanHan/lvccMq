package test;

import data.Message;
import data.MessageType;
import nio.Client;
import utils.IdUtils;

/**
 * @author lvcc
 */
public class TestClient {

    public static void main(String[] args) {
        for (int i = 0; i < 10; i++) {
            new Thread(() -> {
                try {
                    Client client = new Client("127.0.0.1", 81);
                    int megId = IdUtils.getId();
                    String receviedMeg = client.syscSend(new Message(megId, "第" + megId + "条消息", MessageType.PUBLISH_MESSAGE));
                    System.out.println(receviedMeg);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();
        }
    }
}
