package com.zoomigo.whatsapp.batchmailsender.row;

import java.util.Map;

public interface RowHandler {
    Map<String,Object> handle(Map<String,Object> row);
}
