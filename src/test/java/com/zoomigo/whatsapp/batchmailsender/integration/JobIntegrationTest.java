package com.zoomigo.whatsapp.batchmailsender.integration;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mail.javamail.JavaMailSender;

@SpringBootTest(properties = {"spring.flyway.enabled=false"})
public class JobIntegrationTest {

    @MockBean
    private JavaMailSender mailSender;

    @Test
    void contextLoads() {
        // Integration tests (Testcontainers) can be extended here.
    }
}
