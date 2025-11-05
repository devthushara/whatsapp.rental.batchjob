package com.zoomigo.whatsapp.batchmailsender.email;

import com.zoomigo.whatsapp.batchmailsender.config.MailProperties;
import com.zoomigo.whatsapp.batchmailsender.renderer.TemplateRenderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;

import jakarta.mail.Address;
import jakarta.mail.Session;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EmailServiceTest {

    @Mock
    private JavaMailSender mailSender;

    @Mock
    private org.springframework.core.env.Environment env;

    @Mock
    private TemplateRenderer renderer;

    @Test
    void notifyAdmins_sendsOnlyToConfiguredAdmins() throws Exception {
        MailProperties props = new MailProperties();
        props.setAdminRecipients(List.of("thusharadev35@gmail.com", "thushara35@gmail.com"));

        // Provide a real MimeMessage instance for the mocked mailSender to return
        when(mailSender.createMimeMessage()).thenAnswer(invocation -> new MimeMessage(Session.getInstance(new Properties())));
        when(env.getProperty("mail.from", "no-reply@example.com")).thenReturn("no-reply@example.com");

        Map<String,Object> booking = new HashMap<>();
        booking.put("id", 123);
        booking.put("name", "Tester");
        booking.put("startDate", "2025-11-10");
        booking.put("endDate", "2025-11-12");
        booking.put("createdAt", "2025-11-05T09:00:00Z");

        // Stub template rendering
        when(renderer.render("booking-reminder-subject", booking)).thenReturn("New Booking Received");
        when(renderer.render("booking-reminder-body", booking)).thenReturn("<p>Booking received</p>");

        EmailService svc = new EmailService(mailSender, env, props, renderer);

        svc.notifyAdminsForBooking(booking);

        // Capture sent messages
        ArgumentCaptor<MimeMessage> captor = ArgumentCaptor.forClass(MimeMessage.class);
        verify(mailSender, times(2)).send(captor.capture());

        List<MimeMessage> sent = captor.getAllValues();
        Set<String> recipients = new HashSet<>();
        for (MimeMessage m : sent) {
            Address[] addrs = m.getAllRecipients();
            assertNotNull(addrs, "Recipients should not be null");
            for (Address a : addrs) {
                if (a instanceof InternetAddress) {
                    recipients.add(((InternetAddress) a).getAddress());
                } else {
                    recipients.add(a.toString());
                }
            }
            assertEquals("New Booking Received", m.getSubject());
        }

        assertTrue(recipients.contains("thusharadev35@gmail.com"));
        assertTrue(recipients.contains("thushara35@gmail.com"));

        // Ensure we didn't try to use any booking-derived email address
        assertFalse(recipients.contains("Tester"));
    }
}
