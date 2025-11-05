package com.zoomigo.whatsapp.batchmailsender.config;

import org.junit.jupiter.api.Test;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MailStartupValidatorTest {

    @Test
    void run_skipsWhenNotProd() throws Exception {
        Environment env = mock(Environment.class);
        when(env.acceptsProfiles(Profiles.of("prod"))).thenReturn(false);
        MailStartupValidator v = new MailStartupValidator(env);
        v.run(null); // should not throw
    }

    @Test
    void run_throwsWhenMissingPropsInProd() {
        Environment env = mock(Environment.class);
        when(env.acceptsProfiles(Profiles.of("prod"))).thenReturn(true);
        when(env.getProperty("spring.mail.host")).thenReturn(null);
        when(env.getProperty("spring.mail.port")).thenReturn(null);
        when(env.getProperty("spring.mail.username")).thenReturn(null);
        when(env.getProperty("spring.mail.password")).thenReturn(null);

        MailStartupValidator v = new MailStartupValidator(env);
        Exception ex = assertThrows(IllegalStateException.class, () -> v.run(null));
        assertTrue(ex.getMessage().contains("Missing required SMTP configuration"));
    }
}

