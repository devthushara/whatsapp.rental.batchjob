package com.zoomigo.whatsapp.batchmailsender.service;

import com.zoomigo.whatsapp.batchmailsender.jpa.AuditEmail;
import com.zoomigo.whatsapp.batchmailsender.jpa.AuditEmailRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
public class AuditService {
    private static final Logger log = LoggerFactory.getLogger(AuditService.class);
    private final AuditEmailRepository repo;

    public AuditService(AuditEmailRepository repo) {
        this.repo = repo;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void record(Map<String, Object> row, String jobName, String subject, String messageId) {
        try {
            AuditEmail a = new AuditEmail();
            Object ref = row.get("id");
            a.setJobName(jobName);
            a.setReferenceId(ref != null ? ref.toString() : null);
            a.setPayload(row.toString());
            a.setSubject(subject);
            a.setMessageId(messageId);
            repo.save(a);
            log.debug("Recorded audit row id={} ref={}", a.getId(), a.getReferenceId());
        } catch (Exception e) {
            // With REQUIRED propagation, an exception here will roll back the surrounding transaction
            log.error("Failed to record audit for job {} ref {}: {}", jobName, row.get("id"), e.getMessage(), e);
            throw e;
        }
    }
}
