package dev.tiertests;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

import java.nio.charset.StandardCharsets;

public class Consumer {
    private static final String QUEUE_NAME = "server-request";
    private static final String HOST = "192.168.6.126";

    public static void initializeConsumer() throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(HOST);

        // Login to the RabbitMQ server
        factory.setUsername("guest");
        factory.setPassword(""); // redacted

        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        channel.queueDeclare(QUEUE_NAME, false, false, false, null);
        System.out.println(" [*] Waiting for messages. To exit press CTRL+C");

        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
            System.out.println(" [x] Received '" + message + "'");
            consumeMessage(message);
        };

        channel.basicConsume(QUEUE_NAME, true, deliverCallback, consumerTag -> { });
    }

    private static void consumeMessage(String message) {
        if (!checkFormat(message)) {
            System.out.println("Invalid message format");
            return;
        }

        int requestId = Integer.parseInt(message);

        int port = Docker.createContainer("skywars-instance-" + requestId);
        Emitter.containerComplete(requestId, HOST, port);
    }

    private static boolean checkFormat(String message) {
        try {
            Integer.parseInt(message);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
