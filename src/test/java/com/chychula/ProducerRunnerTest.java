package com.chychula;

import com.chychula.message.Message;
import com.chychula.producer.ActiveMqProducer;
import com.chychula.producer.ProducerRunner;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import javax.jms.JMSException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

class ProducerRunnerTest {

    @Test
    void shouldSendAllGeneratedMessages() throws Exception {

        ActiveMqProducer producer1 = mock(ActiveMqProducer.class);
        ActiveMqProducer producer2 = mock(ActiveMqProducer.class);

        ProducerRunner runner = new ProducerRunner() {

            private int producerNumber = 0;

            @Override
            protected ActiveMqProducer createProducer() throws JMSException {
                return ++producerNumber == 1
                        ? producer1
                        : producer2;
            }
        };

        runner.run(1000, 180);

        ArgumentCaptor<Message> captor =
                ArgumentCaptor.forClass(Message.class);

        verify(producer1, atLeast(0)).send(captor.capture());
        verify(producer2, atLeast(0)).send(captor.capture());

        List<Message> sentMessages =
                captor.getAllValues()
                        .stream()
                        .filter(message ->
                                !"__POISON__".equals(message.getName()))
                        .toList();

        assertEquals(
                1000,
                sentMessages.size(),
                "Number of sent POJO messages is incorrect"
        );
    }


    @Test
    void shouldStopGenerationByTimeLimit() throws Exception {

        ActiveMqProducer producer1 = mock(ActiveMqProducer.class);
        ActiveMqProducer producer2 = mock(ActiveMqProducer.class);

        ProducerRunner runner = new ProducerRunner() {

            private int producerNumber = 0;

            @Override
            protected ActiveMqProducer createProducer() throws JMSException {
                return ++producerNumber == 1
                        ? producer1
                        : producer2;
            }
        };

        runner.run(1_000_000, 1);

        ArgumentCaptor<Message> captor =
                ArgumentCaptor.forClass(Message.class);

        verify(producer1, atLeast(0)).send(captor.capture());
        verify(producer2, atLeast(0)).send(captor.capture());

        long sentMessages = captor.getAllValues()
                .stream()
                .filter(message ->
                        !"__POISON__".equals(message.getName()))
                .count();

        assertTrue(
                sentMessages < 1_000_000,
                "Generation was not stopped by time limit"
        );
    }
}