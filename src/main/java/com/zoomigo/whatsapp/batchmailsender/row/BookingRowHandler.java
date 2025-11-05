package com.zoomigo.whatsapp.batchmailsender.row;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class BookingRowHandler implements RowHandler {
    private static final Logger log = LoggerFactory.getLogger(BookingRowHandler.class);

    @Override
    public Map<String, Object> handle(Map<String, Object> row) {
        // Normalize column names to camelCase keys used by templates
        // If DB returns snake_case keys, copy them to camelCase equivalents
        if (row.containsKey("wa_id")) row.put("waId", row.get("wa_id"));
        if (row.containsKey("waId") && !row.containsKey("wa_id")) row.put("wa_id", row.get("waId"));

        if (row.containsKey("start_date")) row.put("startDate", row.get("start_date"));
        if (row.containsKey("startdate")) row.put("startDate", row.get("startdate"));

        if (row.containsKey("end_date")) row.put("endDate", row.get("end_date"));
        if (row.containsKey("enddate")) row.put("endDate", row.get("enddate"));

        if (row.containsKey("created_at")) row.put("createdAt", row.get("created_at"));
        if (row.containsKey("cancelled_at")) row.put("cancelledAt", row.get("cancelled_at"));

        if (row.containsKey("pickup_type")) row.put("pickupType", row.get("pickup_type"));
        if (row.containsKey("delivery_address")) row.put("deliveryAddress", row.get("delivery_address"));

        if (row.containsKey("promo_code")) row.put("promoCodeCode", row.get("promo_code"));

        // If the reader already used AS aliases, ensure promoCodeCode -> promoCode map exists
        Object promoCodeVal = row.get("promoCodeCode");
        if (promoCodeVal != null && !row.containsKey("promoCode")) {
            Map<String,Object> promo = new HashMap<>();
            promo.put("code", promoCodeVal);
            row.put("promoCode", promo);
        }

        // Build a human-friendly bookingPeriod
        String start = row.get("startDate") == null ? "" : row.get("startDate").toString();
        String end = row.get("endDate") == null ? "" : row.get("endDate").toString();
        row.putIfAbsent("bookingPeriod", start + " - " + end);

        log.debug("Normalized booking row for id={}: {}", row.get("id"), row);
        return row;
    }
}
