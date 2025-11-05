package com.zoomigo.whatsapp.batchmailsender.config;

import org.junit.jupiter.api.Test;
import org.springframework.mail.SimpleMailMessage;

import jakarta.mail.internet.MimeMessage;

import static org.junit.jupiter.api.Assertions.*;

class NoopMailSenderTest {

    @Test
    void sendMimeMessage_doesNotThrow() {
        NoopMailSender s = new NoopMailSender();
        MimeMessage m = s.createMimeMessage();
        // should not throw when sending
        s.send(m);
    }

    @Test
    void sendSimpleMailMessage_doesNotThrow() {
        NoopMailSender s = new NoopMailSender();
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setTo("a@example.com");
        msg.setSubject("hi");
        s.send(msg);
    }
}

