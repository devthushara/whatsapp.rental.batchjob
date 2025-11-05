package com.zoomigo.whatsapp.batchmailsender.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;

/**
 * Fallback mail configuration: provides a no-op JavaMailSender bean only when
 * `mail.simulate=true` is set. This avoids accidentally overriding the
 * auto-configured JavaMailSender when real SMTP settings (spring.mail.*) are present.
 */
@Configuration
@ConditionalOnProperty(prefix = "mail", name = "simulate", havingValue = "true", matchIfMissing = false)
public class MailAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(JavaMailSender.class)
    public JavaMailSender javaMailSender() {
        // Return a NoopMailSender (logs sends) when mail.simulate=true
        return new NoopMailSender();
    }
}
