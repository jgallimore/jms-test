/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.superbiz;

import javax.annotation.Resource;
import javax.ejb.Lock;
import javax.ejb.Singleton;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.Topic;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

import static javax.ejb.LockType.READ;

@Singleton
@Lock(READ)
@Path("/send")
public class SenderService {

    @Resource(name = "WMQConnectionFactory")
    protected ConnectionFactory wmqCf;

    @Resource(name = "wmqTopic")
    protected Topic wmqTopic;

    @POST
    @Path("wmq")
    public void wmq(final String payload) throws JMSException {
        process(wmqCf, wmqTopic, payload, Session.SESSION_TRANSACTED);
    }

    protected void process(final ConnectionFactory cf, final Topic topic, final String payload, final int acknowledgeMode) throws JMSException {
        Connection connection = null;
        Session session = null;

        try {
            connection = cf.createConnection();
            connection.start();

            session = connection.createSession(false, acknowledgeMode);
            final MessageProducer producer = session.createProducer(null);

            final TextMessage textMessage = session.createTextMessage(payload);
            textMessage.setObjectProperty("a","b");
            textMessage.setStringProperty("c","D");
            producer.send(topic, textMessage);
        } finally {
            if (session != null) session.close();
            if (connection != null) connection.close();
        }
    }


}
