package com.zoomigo.whatsapp.batchmailsender.renderer;

import org.springframework.stereotype.Component;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.util.Map;

/**
 * Renders subject and body using Thymeleaf templates located in resources/templates/
 */
@Component
public class TemplateRenderer {
    private final SpringTemplateEngine engine;
    public TemplateRenderer(SpringTemplateEngine engine){ this.engine = engine; }

    public String render(String templateName, Map<String, Object> row) {
        Context ctx = new Context();
        ctx.setVariables(row);
        return engine.process(templateName, ctx);
    }
}
