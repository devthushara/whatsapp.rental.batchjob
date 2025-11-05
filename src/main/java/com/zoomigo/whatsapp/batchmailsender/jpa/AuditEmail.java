package com.zoomigo.whatsapp.batchmailsender.jpa;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "audit_email")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuditEmail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "job_name", nullable = false)
    private String jobName;

    @Column(name = "reference_id")
    private String referenceId;

    @Column(columnDefinition = "TEXT")
    private String payload;

    @Column(columnDefinition = "TEXT")
    private String subject;

    @Column(name = "message_id")
    private String messageId;

    @Column(name = "created_at")
    private Instant createdAt = Instant.now();
}
