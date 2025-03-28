package com.rabbit.global.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.client.HttpStatusCodeException;

public class TossErrorUtil {

    public static String extractErrorMessage(HttpStatusCodeException e) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(e.getResponseBodyAsString());
            String msg = root.path("message").asText();
            return msg.isEmpty() ? "결제 실패" : msg;
        } catch (Exception ex) {
            return "결제 실패: " + e.getMessage();
        }
    }
}
