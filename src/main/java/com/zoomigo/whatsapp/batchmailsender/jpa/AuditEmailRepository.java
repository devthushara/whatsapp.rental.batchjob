package com.zoomigo.whatsapp.batchmailsender.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuditEmailRepository extends JpaRepository<AuditEmail, Long> {
}

