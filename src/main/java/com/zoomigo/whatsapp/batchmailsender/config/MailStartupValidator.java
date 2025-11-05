package com.zoomigo.whatsapp.batchmailsender.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import org.springframework.stereotype.Component;

@Component
public class MailStartupValidator implements ApplicationRunner {
    private static final Logger log = LoggerFactory.getLogger(MailStartupValidator.class);
    private final Environment env;

    public MailStartupValidator(Environment env) {
        this.env = env;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        // Only enforce in production profile to avoid breaking tests or local development
        if (!env.acceptsProfiles(Profiles.of("prod"))) {
            log.debug("MailStartupValidator: 'prod' profile not active — skipping strict SMTP validation");
            return;
        }

        log.info("MailStartupValidator: validating SMTP configuration for 'prod' profile");
        String host = env.getProperty("spring.mail.host");
        Integer port = null;
        try {
            String p = env.getProperty("spring.mail.port");
            if (p != null && !p.isBlank()) port = Integer.parseInt(p);
        } catch (Exception ignored) {
        }
        String user = env.getProperty("spring.mail.username");
        String pass = env.getProperty("spring.mail.password");

        StringBuilder problems = new StringBuilder();
        if (host == null || host.isBlank()) problems.append("spring.mail.host must be set; ");
        if (port == null) problems.append("spring.mail.port must be set; ");
        if (user == null || user.isBlank()) problems.append("spring.mail.username must be set; ");
        if (pass == null || pass.isBlank()) problems.append("spring.mail.password must be set; ");

        if (problems.length() > 0) {
            String msg = "Missing required SMTP configuration for 'prod' profile: " + problems.toString() + "\nSet SPRING_MAIL_* env vars or configure application.yml for production.";
            log.error(msg);
            throw new IllegalStateException(msg);
        }

        log.info("MailStartupValidator: SMTP configuration appears present (host={}, port={}, username={})", host, port, user);
    }
}
