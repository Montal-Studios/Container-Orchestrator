package dev.tiertests;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

public class Emitter {
    private static final String MACHINE_IP = "192.168.6.126";
    private static final String QUEUE_NAME = "server-request";

    public static void containerComplete(long requestId, String ip, int port) {
        String message = requestId + "," + ip + "," + port;

        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(MACHINE_IP);
        factory.setUsername("guest");
        factory.setPassword(""); // redacted

        try (Connection connection = factory.newConnection();
             Channel channel = connection.createChannel()) {
            channel.queueDeclare(QUEUE_NAME, false, false, false, null);
            channel.basicPublish("", QUEUE_NAME, null, message.getBytes());
            System.out.println(" [x] Sent '" + message + "'");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
