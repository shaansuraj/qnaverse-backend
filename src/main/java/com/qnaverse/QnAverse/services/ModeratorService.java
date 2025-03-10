package com.qnaverse.QnAverse.services;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class ModeratorService {

    private static final String TEXT_MODERATION_URL = "https://ai-content-moderation-real-time-analysis-and-filtering.p.rapidapi.com/v1/text/moderation";
    private static final String IMAGE_MODERATION_URL = "https://nsfw-images-detection-and-classification.p.rapidapi.com/adult-content";
    private static final String RAPIDAPI_KEY = "310d355a12msh16f886a2fc1d3c5p11657ejsn134aaa546283";
    private static final String TEXT_MODERATION_HOST = "ai-content-moderation-real-time-analysis-and-filtering.p.rapidapi.com";
    private static final String IMAGE_MODERATION_HOST = "nsfw-images-detection-and-classification.p.rapidapi.com";

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public ModeratorService() {
        this.httpClient = HttpClient.newHttpClient();
        this.objectMapper = new ObjectMapper();
    }

    /**
     * Moderates text by calling the RapidAPI text moderation endpoint.
     * Returns a map containing the API response, a Boolean "safe" flag, and if not safe, a list of flagged categories.
     */
    public Map<String, Object> moderateText(String text) throws Exception {
        Map<String, Object> payloadMap = new HashMap<>();
        payloadMap.put("text", text);
        payloadMap.put("threshold", 0.5);
        payloadMap.put("lang", "en");
        String payload = objectMapper.writeValueAsString(payloadMap);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(TEXT_MODERATION_URL))
                .header("x-rapidapi-key", RAPIDAPI_KEY)
                .header("x-rapidapi-host", TEXT_MODERATION_HOST)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(payload))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        Map<String, Object> result = objectMapper.readValue(response.body(), Map.class);

        // Determine safety: for each key (except "ErrorMessage" and "ThresholdValue"),
        // if any category's "ContainsInAppropriateContent" is true, mark text as unsafe.
        boolean safe = true;
        StringBuilder flaggedCategories = new StringBuilder();
        for (Map.Entry<String, Object> entry : result.entrySet()) {
            String key = entry.getKey();
            if ("ErrorMessage".equals(key) || "ThresholdValue".equals(key)) {
                continue;
            }
            if (entry.getValue() instanceof Map) {
                Map<String, Object> category = (Map<String, Object>) entry.getValue();
                Boolean contains = (Boolean) category.get("ContainsInAppropriateContent");
                if (contains != null && contains) {
                    safe = false;
                    flaggedCategories.append(key).append(" ");
                }
            }
        }
        result.put("safe", safe);
        if (!safe) {
            result.put("flaggedCategories", flaggedCategories.toString().trim());
        }
        return result;
    }

    /**
     * Moderates an image by converting the file into a data URL and calling the RapidAPI image moderation endpoint.
     * Returns a map containing the API response and a Boolean "safe" flag.
     */
    public Map<String, Object> moderateImage(MultipartFile file) throws Exception {
        byte[] imageBytes = file.getBytes();
        String base64 = Base64.getEncoder().encodeToString(imageBytes);
        String dataUrl = "data:" + file.getContentType() + ";base64," + base64;
        
        Map<String, Object> payloadMap = new HashMap<>();
        payloadMap.put("url", dataUrl);
        String payload = objectMapper.writeValueAsString(payloadMap);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(IMAGE_MODERATION_URL))
                .header("x-rapidapi-key", RAPIDAPI_KEY)
                .header("x-rapidapi-host", IMAGE_MODERATION_HOST)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(payload))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        Map<String, Object> result = objectMapper.readValue(response.body(), Map.class);

        // Determine safety: if the "unsafe" key is false then the image is safe.
        Boolean unsafe = (Boolean) result.get("unsafe");
        boolean safe = (unsafe != null && !unsafe);
        result.put("safe", safe);
        return result;
    }
}
