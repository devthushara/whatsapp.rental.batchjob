package com.zoomigo.whatsapp.batchmailsender.jpa;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(name = "booking")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookingEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "wa_id")
    private String waId;

    private String name;
    private String bike;
    private Integer duration;
    private Integer price;
    private Integer deposit;
    private String status;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(name = "pickup_type")
    private String pickupType;

    @Column(name = "delivery_address")
    private String deliveryAddress;

    @Column(name = "promo_code")
    private String promoCode;

    @Column(name = "promo_discount_amount")
    private Integer promoDiscountAmount;

    @Column(name = "promo_applied")
    private Boolean promoApplied;

    @Column(name = "currency_unit")
    private String currencyUnit;

    @Column(name = "applied_exchange_rate")
    private BigDecimal appliedExchangeRate;

    @Column(name = "created_at")
    private Instant createdAt = Instant.now();

    @Column(name = "cancelled_at")
    private Instant cancelledAt;

    @Column(name = "batch_sent_at")
    private Instant batchSentAt;

    @Column(name = "batch_attempts")
    private Integer batchAttempts;
}
