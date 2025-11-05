package com.zoomigo.whatsapp.batchmailsender.web;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthController {
    @GetMapping("/healthcheck")
    public String health(){ return "ok"; }
}
