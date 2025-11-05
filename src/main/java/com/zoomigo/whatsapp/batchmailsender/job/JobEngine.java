package com.zoomigo.whatsapp.batchmailsender.job;

import com.zoomigo.whatsapp.batchmailsender.config.BatchProperties;
import com.zoomigo.whatsapp.batchmailsender.email.EmailService;
import com.zoomigo.whatsapp.batchmailsender.jpa.BookingEntity;
import com.zoomigo.whatsapp.batchmailsender.jpa.BookingRepository;
import com.zoomigo.whatsapp.batchmailsender.service.AuditService;
import com.zoomigo.whatsapp.batchmailsender.repository.GenericJdbcReader;
import com.zoomigo.whatsapp.batchmailsender.renderer.TemplateRenderer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class JobEngine {
    private static final Logger log = LoggerFactory.getLogger(JobEngine.class);

    private final GenericJdbcReader reader;
    private final EmailService emailService;
    private final TemplateRenderer renderer;
    private final AuditService auditService;
    private final JdbcTemplate jdbc;
    private final PlatformTransactionManager txManager;
    private final BookingRepository bookingRepo;

    public JobEngine(GenericJdbcReader reader, EmailService emailService, TemplateRenderer renderer, AuditService auditService, JdbcTemplate jdbc, PlatformTransactionManager txManager, BookingRepository bookingRepo) {
        this.reader = reader;
        this.emailService = emailService;
        this.renderer = renderer;
        this.auditService = auditService;
        this.jdbc = jdbc;
        this.txManager = txManager;
        this.bookingRepo = bookingRepo;
    }

    public void runJob(BatchProperties.JobConfig cfg) {
        if ("booking-reminder".equals(cfg.getName())) {
            runJpaBookingJob(cfg);
            return;
        }

        int offset = 0;
        AtomicInteger processed = new AtomicInteger();
        while (true) {
            List<Map<String, Object>> rows = reader.read(cfg.getSql(), cfg.getBatchSize(), offset);
            if (rows.isEmpty()) break;
            for (Map<String, Object> row : rows) {
                processRow(cfg, row);
                processed.incrementAndGet();
            }
            if (rows.size() < cfg.getBatchSize()) break;
            offset += rows.size();
        }
        log.info("Job {} processed {}", cfg.getName(), processed.get());
    }

    private void runJpaBookingJob(BatchProperties.JobConfig cfg) {
        int page = 0;
        AtomicInteger processed = new AtomicInteger();
        while (true) {
            log.info("Loading booking page {} (size={})", page, cfg.getBatchSize());
            Page<BookingEntity> p = bookingRepo.findByBatchSentAtIsNull(PageRequest.of(page, cfg.getBatchSize()));
            log.info("Page {} contains {} bookings", page, p.getNumberOfElements());
            if (p.isEmpty()) break;
            for (BookingEntity b : p.getContent()) {
                Map<String,Object> row = bookingToMap(b);
                log.debug("Processing row from JPA conversion: {}", row);
                processRow(cfg, row);
                processed.incrementAndGet();
            }
            if (!p.hasNext()) break;
            page++;
        }
        log.info("Job {} processed {} (via JPA)", cfg.getName(), processed.get());
    }

    private Map<String,Object> bookingToMap(BookingEntity b) {
        Map<String,Object> m = new HashMap<>();
        m.put("id", b.getId());
        m.put("waId", b.getWaId());
        m.put("name", b.getName());
        m.put("bike", b.getBike());
        m.put("duration", b.getDuration());
        m.put("price", b.getPrice());
        m.put("deposit", b.getDeposit());
        m.put("status", b.getStatus());
        m.put("startDate", b.getStartDate());
        m.put("endDate", b.getEndDate());
        m.put("pickupType", b.getPickupType());
        m.put("deliveryAddress", b.getDeliveryAddress());
        Map<String,Object> promo = new HashMap<>();
        promo.put("code", b.getPromoCode());
        m.put("promoCode", promo);
        m.put("promoDiscountAmount", b.getPromoDiscountAmount());
        m.put("promoApplied", b.getPromoApplied());
        m.put("currencyUnit", b.getCurrencyUnit());
        m.put("appliedExchangeRate", b.getAppliedExchangeRate());
        m.put("createdAt", b.getCreatedAt());
        m.put("cancelledAt", b.getCancelledAt());
        return m;
    }

    private void processRow(BatchProperties.JobConfig cfg, Map<String,Object> row) {
        TransactionTemplate tt = new TransactionTemplate(txManager);
        tt.execute(status -> {
            try {
                Map<String,Object> ctx = row;
                log.info("Processing booking id={}", row.get("id"));
                if (cfg.isDryRun()) {
                    log.info("Dry-run: would notify admins for booking id={}", row.get("id"));
                    return null;
                }

                // Notify admins about the new booking
                emailService.notifyAdminsForBooking(ctx);
                log.info("Notified admins for booking id={}", row.get("id"));

                // mark booking as processed
                if ("booking-reminder".equals(cfg.getName())) {
                    bookingRepo.markProcessed(((Number)row.get("id")).longValue(), Instant.now());
                    log.info("Marked booking id={} as processed ({} set)", row.get("id"), cfg.getMarkColumn());
                } else {
                    jdbc.update("UPDATE booking SET " + cfg.getMarkColumn() + " = now(), " + cfg.getAttemptsColumn() + " = COALESCE(" + cfg.getAttemptsColumn() + ",0) + 1 WHERE id = ?", row.get("id"));
                    log.info("Marked booking id={} as processed ({} set)", row.get("id"), cfg.getMarkColumn());
                }

                auditService.record(row, cfg.getName(), "New Booking Received", null);
            } catch (Exception e) {
                status.setRollbackOnly();
                log.error("Failed processing booking id={}: {}", row.get("id"), e.getMessage(), e);
                throw new RuntimeException(e);
            }
            return null;
        });
    }
}
