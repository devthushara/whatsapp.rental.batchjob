package com.zoomigo.whatsapp.batchmailsender.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.mail.MailProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.core.env.Environment;
import org.springframework.lang.Nullable;

import java.util.Properties;

/**
 * Explicitly configure JavaMailSender so we never fall back to localhost:25.
 * The MailProperties bean from Spring Boot may not always be registered (in some test setups),
 * so accept it as nullable and fall back to values from Environment or sensible defaults.
 */
@Configuration
public class MailConfig {
    private static final Logger log = LoggerFactory.getLogger(MailConfig.class);

    @Bean
    @Primary
    @ConditionalOnMissingBean(JavaMailSender.class)
    public JavaMailSender javaMailSender(@Nullable MailProperties props, Environment env) {
        JavaMailSenderImpl impl = new JavaMailSenderImpl();

        if (props != null) {
            // Use provided properties
            String host = props.getHost() == null || props.getHost().isBlank() ? "smtp.gmail.com" : props.getHost();
            Integer port = props.getPort() == null ? 587 : props.getPort();
            impl.setHost(host);
            impl.setPort(port);
            impl.setUsername(props.getUsername());
            impl.setPassword(props.getPassword());

            Properties javaMailProps = new Properties();
            if (props.getProperties() != null) {
                javaMailProps.putAll(props.getProperties());
            }
            javaMailProps.putIfAbsent("mail.smtp.auth", "true");
            javaMailProps.putIfAbsent("mail.smtp.starttls.enable", "true");
            impl.setJavaMailProperties(javaMailProps);

            log.info("Configured PRIMARY JavaMailSenderImpl host={} port={} username={}", host, port, props.getUsername());
        } else {
            // Fallback: build from Environment or defaults
            String host = env.getProperty("spring.mail.host", "smtp.gmail.com");
            int port = Integer.parseInt(env.getProperty("spring.mail.port", "587"));
            impl.setHost(host);
            impl.setPort(port);
            impl.setUsername(env.getProperty("spring.mail.username"));
            impl.setPassword(env.getProperty("spring.mail.password"));

            Properties javaMailProps = new Properties();
            javaMailProps.put("mail.smtp.auth", env.getProperty("spring.mail.properties.mail.smtp.auth", "true"));
            javaMailProps.put("mail.smtp.starttls.enable", env.getProperty("spring.mail.properties.mail.smtp.starttls.enable", "true"));
            impl.setJavaMailProperties(javaMailProps);

            log.warn("MailProperties not found in context; configured JavaMailSenderImpl from Environment host={} port={}", host, port);
        }

        return impl;
    }
}
