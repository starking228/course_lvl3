package com.chychula;

import com.fasterxml.jackson.databind.ObjectMapper;

import javax.jms.*;

import org.apache.activemq.ActiveMQConnectionFactory;

import java.util.Properties;

public class ActiveMqProducer implements AutoCloseable {

    private static final String BROKER_URL = "tcp://localhost:61616";
//    private static final String QUEUE_NAME = "messagesP.queue";

    private final Connection connection;
    private final Session session;
    private final MessageProducer producer;
//    private final ObjectMapper mapper = new ObjectMapper().findAndRegisterModules();
    private final Properties properties = PropertiesUtil.getLoadedProperties("config.properties");
    private final String QUEUE_NAME = properties.getProperty("QueueName", "defaultName");

private final ObjectMapper mapper = new ObjectMapper()
        .registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());
    public ActiveMqProducer() throws JMSException {
        ConnectionFactory factory = new ActiveMQConnectionFactory(BROKER_URL);
        this.connection = factory.createConnection();
        this.connection.start();

        this.session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        Destination destination = session.createQueue(QUEUE_NAME);
        this.producer = session.createProducer(destination);
        this.producer.setDeliveryMode(DeliveryMode.PERSISTENT);
    }

    public void send(Message message) {
        try {
            String json = mapper.writeValueAsString(message);
            TextMessage textMessage = session.createTextMessage(json);
            producer.send(textMessage);
        } catch (Exception e) {
            throw new RuntimeException("Failed to send message", e);
        }
    }

    @Override
    public void close() throws JMSException {
        producer.close();
        session.close();
        connection.close();
    }
}
