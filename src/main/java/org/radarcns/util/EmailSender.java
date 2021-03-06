/*
 * Copyright 2017 King's College London and The Hyve
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.radarcns.util;

import java.io.IOException;
import java.util.List;
import java.util.Properties;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/**
 * Sends emails.
 */
public class EmailSender {
    private final String from;
    private final List<String> to;
    private final Session session;

    /**
     * Email sender to simple SMTP host. The host must not use authentication
     * @param host smtp host
     * @param port port that the smtp service is configured on
     * @param from MIME From header
     * @param to list of recipients in the MIME To header
     * @throws IOException if a connection cannot be established with the email provider.
     */
    public EmailSender(String host, int port, String from, List<String> to) throws IOException {
        this.from = from;
        this.to = to;

        Properties properties = new Properties();
        // Get system properties
        properties.putAll(System.getProperties());

        if (host != null) {
            // Setup mail server
            properties.setProperty("mail.smtp.host", host);
        }
        if (port > 0) {
            properties.setProperty("mail.smtp.port", String.valueOf(port));
        }

        session = Session.getInstance(properties);
        try {
            Transport transport = session.getTransport("smtp");
            transport.connect();
            if (!transport.isConnected()) {
                throw new IOException("Cannot connect to SMTP server " + host + ":" + port);
            }
        } catch (MessagingException ex) {
            throw new IOException("Cannot instantiate SMTP server", ex);
        }
    }

    /**
     * Send an email with given subject and text. The pre-configured From and To headers are used.
     * @param subject email subject
     * @param text plain text content of the email
     * @throws MessagingException if the message could not be sent
     */
    public void sendEmail(String subject, String text) throws MessagingException {
        // Create a default MimeMessage object.
        MimeMessage message = new MimeMessage(session);

        // Set From: header field of the header.
        message.setFrom(new InternetAddress(from));

        for (String recipient : to) {
            // Set To: header field of the header.
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(recipient));
        }

        // Set Subject: header field
        message.setSubject(subject);

        // Now set the actual message
        message.setText(text);

        // Send message
        Transport.send(message);
    }
}
