package com.zoomigo.whatsapp.batchmailsender.job;

import com.zoomigo.whatsapp.batchmailsender.config.BatchProperties;
import com.zoomigo.whatsapp.batchmailsender.email.EmailService;
import com.zoomigo.whatsapp.batchmailsender.jpa.BookingEntity;
import com.zoomigo.whatsapp.batchmailsender.jpa.BookingRepository;
import com.zoomigo.whatsapp.batchmailsender.renderer.TemplateRenderer;
import com.zoomigo.whatsapp.batchmailsender.service.AuditService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.Instant;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.zoomigo.whatsapp.batchmailsender.repository.GenericJdbcReader;

class JobEngineTest {
    private EmailService emailService;
    private TemplateRenderer renderer;
    private AuditService auditService;
    private JdbcTemplate jdbc;
    private PlatformTransactionManager txManager;
    private BookingRepository bookingRepo;
    private GenericJdbcReader reader;
    private JobEngine engine;

    @BeforeEach
    void setUp() {
        emailService = mock(EmailService.class);
        renderer = mock(TemplateRenderer.class);
        auditService = mock(AuditService.class);
        jdbc = mock(JdbcTemplate.class);
        txManager = mock(PlatformTransactionManager.class);
        bookingRepo = mock(BookingRepository.class);
        reader = mock(GenericJdbcReader.class);
        engine = new JobEngine(reader, emailService, renderer, auditService, jdbc, txManager, bookingRepo);
    }

    @Test
    void runJpaBookingJob_marksProcessedAndNotifiesAdmins() throws Exception {
        // Prepare a single BookingEntity
        BookingEntity b = new BookingEntity();
        b.setId(1L);
        b.setName("Alice");
        b.setStatus("CONFIRMED");
        b.setCreatedAt(Instant.now());

        when(bookingRepo.findByBatchSentAtIsNull(any(org.springframework.data.domain.Pageable.class))).thenReturn(new org.springframework.data.domain.PageImpl<>(List.of(b)));

        BatchProperties.JobConfig cfg = new BatchProperties.JobConfig();
        cfg.setName("booking-reminder");
        cfg.setBatchSize(25);
        cfg.setDryRun(false);
        cfg.setMarkColumn("batch_sent_at");
        cfg.setAttemptsColumn("batch_attempts");

        // Run job
        engine.runJob(cfg);

        // Verify emailService was called
        verify(emailService, times(1)).notifyAdminsForBooking(any(Map.class));
        // Verify bookingRepo.markProcessed called
        verify(bookingRepo, times(1)).markProcessed(eq(1L), any(Instant.class));
        // Verify audit recorded
        verify(auditService, times(1)).record(any(Map.class), eq("booking-reminder"), anyString(), isNull());
    }
}
