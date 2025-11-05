package com.zoomigo.whatsapp.batchmailsender.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@ConfigurationProperties(prefix = "mail")
public class MailProperties {
    private List<String> adminRecipients = new ArrayList<>();

    public List<String> getAdminRecipients() {
        return adminRecipients;
    }

    public void setAdminRecipients(List<String> adminRecipients) {
        this.adminRecipients = adminRecipients;
    }
}

