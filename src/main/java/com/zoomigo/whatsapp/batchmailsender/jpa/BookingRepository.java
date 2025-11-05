package com.zoomigo.whatsapp.batchmailsender.jpa;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

public interface BookingRepository extends JpaRepository<BookingEntity, Long> {
    Page<BookingEntity> findByBatchSentAtIsNull(Pageable pageable);

    @Modifying
    @Transactional
    @Query("UPDATE BookingEntity b SET b.batchSentAt = ?2, b.batchAttempts = COALESCE(b.batchAttempts,0) + 1 WHERE b.id = ?1")
    int markProcessed(Long id, Instant processedAt);
}

