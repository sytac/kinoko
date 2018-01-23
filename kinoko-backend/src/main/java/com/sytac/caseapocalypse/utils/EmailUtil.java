package com.sytac.caseapocalypse.utils;

import com.sun.mail.smtp.SMTPTransport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class EmailUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(EmailUtil.class);


    public static void send(String from, String password, String to, String subject, String body, String smtpServer, String app) throws EmailUtilException {
        try {
            Properties props = System.getProperties();
            props.put("mail.smtps.host", smtpServer);
            props.put("mail.smtps.auth", "true");

            Session session = Session.getInstance(props, null);

            Message msg = new MimeMessage(session);
            msg.setFrom(new InternetAddress(from));
            msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to, false));
            msg.setSubject(subject);
            msg.setText(body);
            msg.setHeader("X-Mailer", app);
            msg.setSentDate(new Date());

            SMTPTransport t = (SMTPTransport) session.getTransport("smtps");
            t.connect(smtpServer, from, password);
            t.sendMessage(msg, msg.getAllRecipients());
            LOGGER.debug("Email sent, response: " + t.getLastServerResponse());
            t.close();
        } catch (MessagingException e) {
            LOGGER.error("Impossible to send the email");
            throw new EmailUtilException("Impossible to send the email " + e);
        }

    }





}