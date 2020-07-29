package test;

import nio.DefaultRequestProcessor;
import nio.DefaultResponseProcessor;
import nio.RequestProcessor;
import nio.ResponseProcessor;
import nio.Server;

/**
 * @author lvcc
 */
public class TestServer {

    public static void main(String[] args) {
        RequestProcessor requestProcessor = new DefaultRequestProcessor();
        ResponseProcessor responseProcessor = new DefaultResponseProcessor();
        try {
            Server testServer = new Server(81, requestProcessor, responseProcessor);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
