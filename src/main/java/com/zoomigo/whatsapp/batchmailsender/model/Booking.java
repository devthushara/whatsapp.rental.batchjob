package com.zoomigo.whatsapp.batchmailsender.model;

import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Booking {
    private Long id;
    private String waId;
    private String name;
    private String bike;
    private Integer duration;
    private Integer price;
    private Integer deposit;
    private String status; // e.g., CONFIRMED, CANCELLED, COMPLETED
    private LocalDate startDate;
    private LocalDate endDate;
    private String pickupType;
    private String deliveryAddress;
    private PromoCode promoCode;
    private Integer promoDiscountAmount;
    private Boolean promoApplied = false;
    private String currencyUnit;
    private BigDecimal appliedExchangeRate;
    private Instant createdAt;
    private Instant cancelledAt;
}
