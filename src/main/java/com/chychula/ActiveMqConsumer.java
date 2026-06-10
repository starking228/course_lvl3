package com.chychula;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;
import java.util.Properties;

public class ActiveMqConsumer implements AutoCloseable {

    private final Connection connection;
    private final Session session;
    private final MessageConsumer consumer;
    private final ObjectMapper mapper = new ObjectMapper()
            .registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());
    private final Properties properties = PropertiesUtil.getLoadedProperties("config.properties");
    private final String QUEUE_NAME = properties.getProperty("QueueName", "defaultName");
    private final String BROKER_URL = properties.getProperty("BrokerURL", "tcp://localhost:61616");


    public ActiveMqConsumer() throws JMSException {
        ConnectionFactory factory = new ActiveMQConnectionFactory(BROKER_URL);
        this.connection = factory.createConnection();
        this.connection.start();

        this.session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        Destination destination = session.createQueue(QUEUE_NAME);
        this.consumer = session.createConsumer(destination);
    }

    public Message receive() throws JMSException {
        javax.jms.Message jmsMsg = consumer.receive(2000);

        if (jmsMsg == null) {
            return null;
        }

        if (!(jmsMsg instanceof TextMessage)) {
            throw new RuntimeException("Unsupported JMS message type: " + jmsMsg);
        }

        String json;
        try {
            json = ((TextMessage) jmsMsg).getText();
            return mapper.readValue(json, Message.class);
        } catch (Exception e) {
            throw new RuntimeException("Failed to deserialize message", e);
        }
    }

    @Override
    public void close() throws Exception {
        consumer.close();
        session.close();
        connection.close();
    }
}
