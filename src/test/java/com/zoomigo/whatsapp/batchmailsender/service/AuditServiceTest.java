package com.zoomigo.whatsapp.batchmailsender.service;

import com.zoomigo.whatsapp.batchmailsender.jpa.AuditEmail;
import com.zoomigo.whatsapp.batchmailsender.jpa.AuditEmailRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuditServiceTest {
    private AuditEmailRepository repo;
    private AuditService svc;

    @BeforeEach
    void setUp() {
        repo = mock(AuditEmailRepository.class);
        svc = new AuditService(repo);
    }

    @Test
    void record_savesAuditEmail() {
        Map<String,Object> row = new HashMap<>();
        row.put("id", 42);
        row.put("name", "Tester");

        svc.record(row, "booking-reminder", "subject", "msg-1");

        ArgumentCaptor<AuditEmail> cap = ArgumentCaptor.forClass(AuditEmail.class);
        verify(repo).save(cap.capture());
        AuditEmail a = cap.getValue();
        assertEquals("booking-reminder", a.getJobName());
        assertEquals("42", a.getReferenceId());
        assertEquals("subject", a.getSubject());
        assertEquals("msg-1", a.getMessageId());
        assertTrue(a.getPayload().contains("Tester"));
    }
}

