package com.zoomigo.whatsapp.batchmailsender.email;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.internet.MimeMessage;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.zoomigo.whatsapp.batchmailsender.config.MailProperties;
import com.zoomigo.whatsapp.batchmailsender.config.SimulatedMailSender;
import com.zoomigo.whatsapp.batchmailsender.renderer.TemplateRenderer;

/**
 * Email service that notifies admins about new bookings.
 */
@Service
public class EmailService {
    private static final Logger log = LoggerFactory.getLogger(EmailService.class);
    private final JavaMailSender mailSender;
    private final String from;
    private final MailProperties mailProps;
    private final TemplateRenderer renderer;
    private final boolean simulated;

    public EmailService(JavaMailSender mailSender, org.springframework.core.env.Environment env, MailProperties mailProps, TemplateRenderer renderer){
        this.mailSender = mailSender;
        this.from = env.getProperty("mail.from", "no-reply@example.com");
        this.mailProps = mailProps;
        this.renderer = renderer;

        boolean sim = mailSender instanceof SimulatedMailSender;
        // If JavaMailSenderImpl exists but looks unconfigured (no host or points to localhost), treat as simulated
        if (mailSender instanceof JavaMailSenderImpl) {
            try {
                JavaMailSenderImpl impl = (JavaMailSenderImpl) mailSender;
                String host = impl.getHost();
                int port = impl.getPort();
                String user = impl.getUsername();
                if (host == null || host.isBlank() || "localhost".equalsIgnoreCase(host) || "127.0.0.1".equals(host) || (port == 25 && (user == null || user.isBlank()))) {
                    log.warn("Detected JavaMailSenderImpl with unconfigured host='{}' port={} user='{}'. Treating mail sending as SIMULATED to avoid connection attempts.", host, port, user);
                    sim = true;
                }
            } catch (Exception e) {
                log.warn("Unable to introspect JavaMailSenderImpl; proceeding with provided instance: {}", e.getMessage());
            }
        }
        this.simulated = sim;

        // Diagnostic info: log mail sender implementation and SMTP settings (if available)
        try {
            log.info("MailSender implementation: {}{}", mailSender.getClass().getName(), simulated ? " (SIMULATED)" : "");
            if (mailSender instanceof JavaMailSenderImpl) {
                JavaMailSenderImpl impl = (JavaMailSenderImpl) mailSender;
                log.info("JavaMailSenderImpl config - host={}, port={}, username={}", impl.getHost(), impl.getPort(), impl.getUsername());
            }
            if (simulated) {
                log.warn("MAIL SENDING IS SIMULATED: No network sends will occur. To enable real SMTP, set spring.mail.host, spring.mail.port, spring.mail.username and spring.mail.password (or set mail.simulate=true for an explicit simulator).");
            }
        } catch (Exception e) {
            log.warn("Failed to log mail sender details", e);
        }
    }

    /**
     * Notify all admins about a new booking. Sends one email per admin recipient.
     * The email subject is "New Booking Received" and the body contains booking details.
     */
    public void notifyAdminsForBooking(Map<String,Object> booking) throws Exception {
        List<String> admins = mailProps.getAdminRecipients();
        if (admins == null || admins.isEmpty()) {
            log.warn("No admin recipients configured; skipping notification for booking id={}", booking.get("id"));
            return;
        }

        log.info("Preparing notification for booking id={} to admins={}", booking.get("id"), admins);

        String subject;
        try {
            subject = renderer.render("booking-reminder-subject", booking).trim();
            log.debug("Rendered subject: {}", subject);
        } catch (Exception e) {
            subject = "New Booking Received";
            log.warn("Failed to render subject template, using fallback subject='{}'", subject, e);
        }

        String body;
        try {
            body = renderer.render("booking-reminder-body", booking);
            log.debug("Rendered body length={} chars", body == null ? 0 : body.length());
        } catch (Exception e) {
            log.warn("Failed to render template booking-reminder-body, falling back to plain text", e);
            body = buildBookingBody(booking);
            log.debug("Fallback body length={} chars", body == null ? 0 : body.length());
        }

        // detailed pre-send debug
        log.info("About to send notification for booking id={} to {} admin(s)", booking.get("id"), admins.size());
        log.debug("Mail From: {} | Subject: {} | Body snippet: {}", from, subject, body == null ? "" : body.substring(0, Math.min(120, body.length())));

        for (String admin : admins) {
            try {
                log.debug("Sending email to admin={} for booking id={}", admin, booking.get("id"));
                String msgId = sendToRecipient(admin, subject, body);
                if (simulated) {
                    log.info("Notification simulation recorded for admin {} for booking id={} (simMsgId={})", admin, booking.get("id"), msgId);
                } else {
                    log.info("Notification sent to admin {} for booking id={} (msgId={})", admin, booking.get("id"), msgId);
                }
            } catch (Exception e) {
                log.error("Failed to send notification to admin {} for booking id={}", admin, booking.get("id"), e);
                throw e; // bubble up so caller can decide to rollback
            }
        }
    }

    private String buildBookingBody(Map<String,Object> b) {
        StringBuilder sb = new StringBuilder();
        sb.append("A new booking has been received:\n\n");
        sb.append("ID: ").append(b.get("id")).append("\n");
        sb.append("Name: ").append(b.get("name")).append("\n");
        sb.append("WA ID: ").append(b.getOrDefault("waId", b.get("wa_id"))).append("\n");
        sb.append("Bike: ").append(b.get("bike")).append("\n");
        sb.append("Duration: ").append(b.get("duration")).append("\n");
        sb.append("Price: ").append(b.get("price")).append("\n");
        sb.append("Deposit: ").append(b.get("deposit")).append("\n");
        sb.append("Status: ").append(b.get("status")).append("\n");
        sb.append("Start Date: ").append(b.getOrDefault("startDate", b.get("startdate"))).append("\n");
        sb.append("End Date: ").append(b.getOrDefault("endDate", b.get("enddate"))).append("\n");
        sb.append("Pickup Type: ").append(b.get("pickupType")).append("\n");
        sb.append("Delivery Address: ").append(b.get("deliveryAddress")).append("\n");
        Object promo = b.get("promoCode");
        if (promo instanceof Map) {
            sb.append("Promo Code: ").append(((Map)promo).get("code")).append("\n");
        } else {
            sb.append("Promo Code: ").append(b.get("promoCodeCode")).append("\n");
        }
        sb.append("Promo Discount Amount: ").append(b.get("promoDiscountAmount")).append("\n");
        sb.append("Promo Applied: ").append(b.get("promoApplied")).append("\n");
        sb.append("Currency Unit: ").append(b.get("currencyUnit")).append("\n");
        sb.append("Applied Exchange Rate: ").append(b.get("appliedExchangeRate")).append("\n");
        sb.append("Created At: ").append(b.getOrDefault("createdAt", b.get("created_at"))).append("\n");
        sb.append("Cancelled At: ").append(b.getOrDefault("cancelledAt", b.get("cancelled_at"))).append("\n");
        return sb.toString();
    }

    @Retry(name = "emailSender")
    @CircuitBreaker(name = "emailSender")
    private String sendToRecipient(String to, String subject, String body) throws Exception {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        helper.setTo(to);
        helper.setFrom(from);
        helper.setSubject(subject);
        helper.setText(body, false);
        if (!simulated) {
            mailSender.send(message);
        } else {
            // do not attempt to send over network; log simulated send details
            try {
                String snippet = body == null ? "" : body.substring(0, Math.min(120, body.length()));
                log.info("[SIMULATED SEND] To: {} | Subject: {} | Body snippet: {}", to, subject, snippet);
            } catch (Exception ignored) {}
        }
        String msgId = (simulated ? "SIMULATED-" : "") + UUID.randomUUID().toString();
        log.debug("Sent mail to {} subject={} id={}", to, subject, msgId);
        return msgId;
    }
}
