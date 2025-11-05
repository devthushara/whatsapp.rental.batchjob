package com.zoomigo.whatsapp.batchmailsender.unit;

import com.zoomigo.whatsapp.batchmailsender.renderer.TemplateRenderer;
import com.zoomigo.whatsapp.batchmailsender.config.BatchProperties;
import com.zoomigo.whatsapp.batchmailsender.job.JobEngine;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertTrue;

@WebMvcTest
public class TemplateRendererTest {
    @MockBean
    private BatchProperties batchProperties;

    @MockBean
    private JobEngine jobEngine;

    @Autowired
    private SpringTemplateEngine engine;

    @Test
    void smoke() {
        TemplateRenderer r = new TemplateRenderer(engine);
        String out = r.render("booking-reminder-subject", Map.of("id", 1, "status", "CONFIRMED"));
        assertTrue(out.contains("booking"));
    }
}
