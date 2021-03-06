package common;

import data.Node;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Common {

    public static Charset DEFAULT_CHAR_SETS = StandardCharsets.ISO_8859_1;

    public static Node BROKER_NODE = new Node("127.0.0.1", 81);

    public static Node CONSUMER_NODE = new Node("127.0.0.1", 8078);

    public static ExecutorService executorService = Executors.newFixedThreadPool(100);
}
