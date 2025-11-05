package com.zoomigo.whatsapp.batchmailsender.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.mail.javamail.JavaMailSender;

/**
 * Development mail configuration that provides a no-op mail sender when the 'local' profile is active.
 */
@Configuration
@Profile("local")
public class DevMailConfig {

    @Bean
    public JavaMailSender noopMailSender() {
        return new NoopMailSender();
    }
}


