package org.superbiz;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;
import java.util.logging.Logger;

public class WMQReceiver implements MessageListener {

    private final Logger logger = Logger.getLogger(WMQReceiver.class.getName());

    @Resource(name = "WMQConnectionFactory")
    protected ConnectionFactory wmqCf;

    @PostConstruct
    public void start() {
        logger.info("*** WMQReceiver MDB started ***");
    }

    @Override
    public void onMessage(Message message) {

        if (wmqCf == null) {
            logger.severe("*** WMQ Receiver - Outbound connection factory is null");
        }

        try {
            if (TextMessage.class.isInstance(message)) {
                final TextMessage textMessage = TextMessage.class.cast(message);
                logger.info("Hello from Websphere MQ! Message received: " + textMessage.getText());
                logger.info("a: " + textMessage.getObjectProperty("a").toString());
                logger.info("c: " + textMessage.getStringProperty("c"));
            }
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}
