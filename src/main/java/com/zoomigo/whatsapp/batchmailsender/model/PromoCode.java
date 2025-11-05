package com.zoomigo.whatsapp.batchmailsender.model;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PromoCode {
    private String code;
    private String description;
}
