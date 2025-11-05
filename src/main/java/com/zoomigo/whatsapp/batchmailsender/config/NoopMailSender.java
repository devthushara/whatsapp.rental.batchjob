package com.zoomigo.whatsapp.batchmailsender.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessagePreparator;

import jakarta.mail.Address;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

/**
 * A no-op JavaMailSender for development that logs sends instead of performing network calls.
 */
public class NoopMailSender implements JavaMailSender, SimulatedMailSender {
    private static final Logger log = LoggerFactory.getLogger(NoopMailSender.class);

    @Override
    public MimeMessage createMimeMessage() {
        return new MimeMessage((jakarta.mail.Session) null);
    }

    @Override
    public MimeMessage createMimeMessage(java.io.InputStream contentStream) throws MailException {
        try {
            return new MimeMessage(null, contentStream);
        } catch (Exception e) {
            throw new MailException("Failed to create MimeMessage from stream") {};
        }
    }

    @Override
    public void send(MimeMessage mimeMessage) throws MailException {
        try {
            Address[] recipients = mimeMessage.getAllRecipients();
            StringBuilder recips = new StringBuilder();
            if (recipients != null) {
                for (Address a : recipients) {
                    if (a instanceof InternetAddress) recips.append(((InternetAddress) a).getAddress()).append(",");
                    else recips.append(a.toString()).append(",");
                }
            }
            String subj = "(unknown)";
            try { subj = mimeMessage.getSubject(); } catch (Exception ignored) {}
            log.info("[NOOP MAIL - SIMULATED] Would send message - to: {} subject: {}", recips.toString(), subj);
        } catch (Exception e) {
            log.info("[NOOP MAIL - SIMULATED] Would send message (unable to read recipients/subject)");
        }
    }

    @Override
    public void send(MimeMessage... mimeMessages) throws MailException {
        for (MimeMessage m : mimeMessages) send(m);
    }

    @Override
    public void send(SimpleMailMessage simpleMessage) throws MailException {
        if (simpleMessage == null) return;
        log.info("[NOOP MAIL - SIMULATED] Would send SimpleMailMessage to: {} subject: {}", (Object) simpleMessage.getTo(), simpleMessage.getSubject());
    }

    @Override
    public void send(SimpleMailMessage... simpleMessages) throws MailException {
        if (simpleMessages == null) return;
        for (SimpleMailMessage m : simpleMessages) send(m);
    }

    @Override
    public void send(MimeMessagePreparator mimeMessagePreparator) throws MailException {
        if (mimeMessagePreparator == null) return;
        try {
            MimeMessage msg = createMimeMessage();
            mimeMessagePreparator.prepare(msg);
            send(msg);
        } catch (Exception e) {
            throw new MailException("Failed to prepare or send message") {};
        }
    }

    @Override
    public void send(MimeMessagePreparator... mimeMessagePreparators) throws MailException {
        if (mimeMessagePreparators == null) return;
        for (MimeMessagePreparator p : mimeMessagePreparators) send(p);
    }

    // All required methods implemented; this no-op sender logs recipients instead of performing network calls.
}
