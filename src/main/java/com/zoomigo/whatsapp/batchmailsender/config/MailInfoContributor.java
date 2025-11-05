package com.zoomigo.whatsapp.batchmailsender.config;

import org.springframework.boot.actuate.info.Info;
import org.springframework.boot.actuate.info.InfoContributor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class MailInfoContributor implements InfoContributor {
    private final JavaMailSender mailSender;

    public MailInfoContributor(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Override
    public void contribute(Info.Builder builder) {
        Map<String, Object> m = new HashMap<>();
        if (mailSender == null) {
            m.put("present", false);
        } else {
            m.put("present", true);
            m.put("implementation", mailSender.getClass().getName());
            boolean simulated = mailSender instanceof SimulatedMailSender;
            m.put("simulated", simulated);
            if (mailSender instanceof JavaMailSenderImpl) {
                JavaMailSenderImpl impl = (JavaMailSenderImpl) mailSender;
                m.put("host", impl.getHost());
                m.put("port", impl.getPort());
                m.put("username", impl.getUsername());
            }
        }
        builder.withDetail("mail", m);
    }
}

