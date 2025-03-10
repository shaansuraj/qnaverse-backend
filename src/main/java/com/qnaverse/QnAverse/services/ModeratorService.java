package com.qnaverse.QnAverse.services;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class ModeratorService {

    // Sightengine API endpoints and credentials
    private static final String TEXT_MODERATION_URL = "https://api.sightengine.com/1.0/text/check.json";
    private static final String IMAGE_MODERATION_URL = "https://api.sightengine.com/1.0/check.json";
    private static final String API_USER = "484426288";
    private static final String API_SECRET = "xLem8sS9BRg8HTQn7EchYXMPxnqJdqJy";

    // Thresholds for safe content determination
    private static final double TEXT_THRESHOLD = 0.5;
    // For image moderation: "none" (nudity) should be high, and other categories should not exceed a general threshold
    private static final double IMAGE_NONE_THRESHOLD = 0.8;
    private static final double IMAGE_GENERAL_THRESHOLD = 0.5;

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public ModeratorService() {
        this.httpClient = HttpClient.newHttpClient();
        this.objectMapper = new ObjectMapper();
    }

    /**
     * Moderates text by sending a URL-encoded POST request to Sightengine’s text endpoint.
     * It checks the returned "moderation_classes" object for any category whose score >= TEXT_THRESHOLD.
     * Returns a Map with:
     *   "safe": Boolean
     *   "flaggedCategories": (if unsafe)
     */
    public Map<String, Object> moderateText(String text) throws Exception {
        // Build request body (application/x-www-form-urlencoded)
        StringBuilder requestBody = new StringBuilder();
        requestBody.append("text=").append(URLEncoder.encode(text, StandardCharsets.UTF_8));
        requestBody.append("&lang=").append(URLEncoder.encode("en", StandardCharsets.UTF_8));
        requestBody.append("&models=").append(URLEncoder.encode("general,self-harm", StandardCharsets.UTF_8));
        requestBody.append("&mode=").append(URLEncoder.encode("ml", StandardCharsets.UTF_8));
        requestBody.append("&api_user=").append(URLEncoder.encode(API_USER, StandardCharsets.UTF_8));
        requestBody.append("&api_secret=").append(URLEncoder.encode(API_SECRET, StandardCharsets.UTF_8));

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(TEXT_MODERATION_URL))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody.toString()))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        Map<String, Object> result = objectMapper.readValue(response.body(), Map.class);

        boolean safe = true;
        List<String> flaggedCategories = new ArrayList<>();

        // Evaluate "moderation_classes" if present
        if (result.containsKey("moderation_classes")) {
            Object modClassesObj = result.get("moderation_classes");
            if (modClassesObj instanceof Map) {
                Map<String, Object> modClasses = (Map<String, Object>) modClassesObj;
                for (Map.Entry<String, Object> entry : modClasses.entrySet()) {
                    String key = entry.getKey();
                    if ("available".equals(key)) continue; // skip "available" array
                    double score = 0.0;
                    try {
                        score = ((Number) entry.getValue()).doubleValue();
                    } catch (Exception e) {
                        continue;
                    }
                    if (score >= TEXT_THRESHOLD) {
                        safe = false;
                        flaggedCategories.add(key);
                    }
                }
            }
        }

        result.put("safe", safe);
        if (!safe) {
            result.put("flaggedCategories", String.join(" ", flaggedCategories));
        }
        return result;
    }

    /**
     * Moderates an image by sending a POST request with the base64-encoded image to Sightengine’s /check.json endpoint.
     * The response is analyzed:
     *   - "nudity" → if "none" < IMAGE_NONE_THRESHOLD or any subcategory >= IMAGE_GENERAL_THRESHOLD → flagged
     *   - other categories (weapon, alcohol, etc.) → if prob >= IMAGE_GENERAL_THRESHOLD → flagged
     * Returns a Map with:
     *   "safe": Boolean
     *   "flaggedCategories": (if unsafe)
     */
    // public Map<String, Object> moderateImage(MultipartFile file) throws Exception {
    //     // Convert image to base64
    //     byte[] imageBytes = file.getBytes();
    //     String base64 = Base64.getEncoder().encodeToString(imageBytes);

    //     // Build request body for application/x-www-form-urlencoded
    //     String modelsParam = "nudity-2.1,weapon,alcohol,recreational_drug,medical,offensive-2.0,scam,text-content," +
    //                          "gore-2.0,qr-content,tobacco,violence,self-harm,money,gambling";
    //     StringBuilder requestBody = new StringBuilder();
    //     requestBody.append("api_user=").append(URLEncoder.encode(API_USER, StandardCharsets.UTF_8));
    //     requestBody.append("&api_secret=").append(URLEncoder.encode(API_SECRET, StandardCharsets.UTF_8));
    //     requestBody.append("&models=").append(URLEncoder.encode(modelsParam, StandardCharsets.UTF_8));
    //     // Pass the base64 data with a "media" parameter
    //     requestBody.append("&media=").append(URLEncoder.encode("base64," + base64, StandardCharsets.UTF_8));

    //     HttpRequest request = HttpRequest.newBuilder()
    //             .uri(URI.create(IMAGE_MODERATION_URL))
    //             .header("Content-Type", "application/x-www-form-urlencoded")
    //             .POST(HttpRequest.BodyPublishers.ofString(requestBody.toString()))
    //             .build();

    //     HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
    //     Map<String, Object> result = objectMapper.readValue(response.body(), Map.class);

    //     boolean safe = true;
    //     List<String> flaggedCategories = new ArrayList<>();

    //     // 1) Check "nudity" -> ensure "none" is high enough
    //     if (result.containsKey("nudity") && result.get("nudity") instanceof Map) {
    //         Map<String, Object> nudity = (Map<String, Object>) result.get("nudity");
    //         if (nudity.containsKey("none")) {
    //             double noneScore = ((Number) nudity.get("none")).doubleValue();
    //             if (noneScore < IMAGE_NONE_THRESHOLD) {
    //                 safe = false;
    //                 flaggedCategories.add("nudity");
    //             }
    //         }
    //         // Check subcategories for any >= IMAGE_GENERAL_THRESHOLD
    //         for (Map.Entry<String, Object> entry : nudity.entrySet()) {
    //             String key = entry.getKey();
    //             if ("none".equals(key)) continue;
    //             try {
    //                 double score = ((Number) entry.getValue()).doubleValue();
    //                 if (score >= IMAGE_GENERAL_THRESHOLD) {
    //                     safe = false;
    //                     if (!flaggedCategories.contains("nudity")) {
    //                         flaggedCategories.add("nudity");
    //                     }
    //                     break;
    //                 }
    //             } catch (Exception e) {
    //                 // skip non-numeric
    //             }
    //         }
    //     }

    //     // 2) Check other sensitive categories
    //     String[] categories = { "weapon", "recreational_drug", "medical", "alcohol", "offensive", "scam",
    //                             "gore", "violence", "self-harm", "money", "gambling" };
    //     for (String category : categories) {
    //         if (result.containsKey(category)) {
    //             Object catObj = result.get(category);
    //             if (catObj instanceof Map) {
    //                 Map<String, Object> catMap = (Map<String, Object>) catObj;
    //                 // If the category has a "prob" field, check it
    //                 if (catMap.containsKey("prob")) {
    //                     double prob = ((Number) catMap.get("prob")).doubleValue();
    //                     if (prob >= IMAGE_GENERAL_THRESHOLD) {
    //                         safe = false;
    //                         flaggedCategories.add(category);
    //                     }
    //                 } else if (catMap.containsKey("classes")) {
    //                     // For categories that have sub-classes, find max
    //                     Map<String, Object> classes = (Map<String, Object>) catMap.get("classes");
    //                     double maxScore = 0.0;
    //                     for (Object value : classes.values()) {
    //                         try {
    //                             double score = ((Number) value).doubleValue();
    //                             if (score > maxScore) {
    //                                 maxScore = score;
    //                             }
    //                         } catch (Exception e) { }
    //                     }
    //                     if (maxScore >= IMAGE_GENERAL_THRESHOLD) {
    //                         safe = false;
    //                         flaggedCategories.add(category);
    //                     }
    //                 }
    //             }
    //         }
    //     }

    //     result.put("safe", safe);
    //     if (!safe) {
    //         result.put("flaggedCategories", String.join(" ", flaggedCategories));
    //     }
    //     return result;
    // }


    /**
     * Moderates an image by sending a GET request with the "url" param set to the uploaded mediaUrl.
     * The response is analyzed:
     *   - "nudity" → if "none" < IMAGE_NONE_THRESHOLD or any subcategory >= IMAGE_GENERAL_THRESHOLD → flagged
     *   - other categories (weapon, gore, etc.) → if prob >= IMAGE_GENERAL_THRESHOLD → flagged
     * Returns a Map with:
     *   "safe": Boolean
     *   "flaggedCategories": (if unsafe)
     */
    public Map<String, Object> moderateImageUrl(String mediaUrl) throws Exception {
        // Build GET request with the param "url" = mediaUrl
        String modelsParam = "nudity-2.1,weapon,alcohol,recreational_drug,medical,offensive-2.0,scam," +
                             "text-content,gore-2.0,qr-content,tobacco,violence,self-harm,money,gambling";

        String urlWithParams = IMAGE_MODERATION_URL
            + "?url=" + URLEncoder.encode(mediaUrl, StandardCharsets.UTF_8)
            + "&models=" + URLEncoder.encode(modelsParam, StandardCharsets.UTF_8)
            + "&api_user=" + URLEncoder.encode(API_USER, StandardCharsets.UTF_8)
            + "&api_secret=" + URLEncoder.encode(API_SECRET, StandardCharsets.UTF_8);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(urlWithParams))
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        Map<String, Object> result = objectMapper.readValue(response.body(), Map.class);

        boolean safe = true;
        List<String> flaggedCategories = new ArrayList<>();

        // Evaluate "nudity"
        if (result.containsKey("nudity") && result.get("nudity") instanceof Map) {
            Map<String, Object> nudity = (Map<String, Object>) result.get("nudity");
            // "none" should be high
            if (nudity.containsKey("none")) {
                double noneScore = ((Number) nudity.get("none")).doubleValue();
                if (noneScore < IMAGE_NONE_THRESHOLD) {
                    safe = false;
                    flaggedCategories.add("nudity");
                }
            }
            // Check subcategories
            for (Map.Entry<String, Object> entry : nudity.entrySet()) {
                String key = entry.getKey();
                if ("none".equals(key)) continue;
                try {
                    double score = ((Number) entry.getValue()).doubleValue();
                    if (score >= IMAGE_GENERAL_THRESHOLD) {
                        safe = false;
                        if (!flaggedCategories.contains("nudity")) {
                            flaggedCategories.add("nudity");
                        }
                        break;
                    }
                } catch (Exception e) {
                    // skip non-numeric
                }
            }
        }

        // Evaluate other categories
        String[] categories = { "weapon", "recreational_drug", "medical", "alcohol", "offensive", "scam",
                                "gore", "violence", "self-harm", "money", "gambling" };
        for (String category : categories) {
            if (result.containsKey(category)) {
                Object catObj = result.get(category);
                if (catObj instanceof Map) {
                    Map<String, Object> catMap = (Map<String, Object>) catObj;
                    if (catMap.containsKey("prob")) {
                        double prob = ((Number) catMap.get("prob")).doubleValue();
                        if (prob >= IMAGE_GENERAL_THRESHOLD) {
                            safe = false;
                            flaggedCategories.add(category);
                        }
                    } else if (catMap.containsKey("classes")) {
                        Map<String, Object> classes = (Map<String, Object>) catMap.get("classes");
                        double maxScore = 0.0;
                        for (Object value : classes.values()) {
                            try {
                                double score = ((Number) value).doubleValue();
                                if (score > maxScore) {
                                    maxScore = score;
                                }
                            } catch (Exception e) { }
                        }
                        if (maxScore >= IMAGE_GENERAL_THRESHOLD) {
                            safe = false;
                            flaggedCategories.add(category);
                        }
                    }
                }
            }
        }

        result.put("safe", safe);
        if (!safe) {
            result.put("flaggedCategories", String.join(" ", flaggedCategories));
        }
        return result;
    }
}


// package com.qnaverse.QnAverse.services;

// import java.net.URI;
// import java.net.URLEncoder;
// import java.net.http.HttpClient;
// import java.net.http.HttpRequest;
// import java.net.http.HttpResponse;
// import java.nio.charset.StandardCharsets;
// import java.util.ArrayList;
// import java.util.Base64;
// import java.util.List;
// import java.util.Map;

// import org.springframework.stereotype.Service;
// import org.springframework.web.multipart.MultipartFile;

// import com.fasterxml.jackson.databind.ObjectMapper;

// @Service
// public class ModeratorService {

//     // New Sightengine API endpoints and credentials
//     private static final String TEXT_MODERATION_URL = "https://api.sightengine.com/1.0/text/check.json";
//     private static final String IMAGE_MODERATION_URL = "https://api.sightengine.com/1.0/check.json";
//     private static final String API_USER = "484426288";
//     private static final String API_SECRET = "xLem8sS9BRg8HTQn7EchYXMPxnqJdqJy";
    
//     // Thresholds for safe content determination
//     private static final double TEXT_THRESHOLD = 0.5;
//     // For image moderation: for "nudity", the "none" score should be high; for other categories use a general threshold.
//     private static final double IMAGE_NONE_THRESHOLD = 0.8; 
//     private static final double IMAGE_GENERAL_THRESHOLD = 0.5;
    
//     private final HttpClient httpClient;
//     private final ObjectMapper objectMapper;

//     public ModeratorService() {
//         this.httpClient = HttpClient.newHttpClient();
//         this.objectMapper = new ObjectMapper();
//     }
    
//     /**
//      * Moderates text by sending a URL‑encoded POST request to Sightengine’s text endpoint.
//      * It checks the returned moderation_classes scores against a threshold and returns a map
//      * containing a Boolean "safe" flag and, if unsafe, a list of flagged categories.
//      */
//     public Map<String, Object> moderateText(String text) throws Exception {
//         // Build request body (x-www-form-urlencoded)
//         StringBuilder requestBody = new StringBuilder();
//         requestBody.append("text=").append(URLEncoder.encode(text, StandardCharsets.UTF_8));
//         requestBody.append("&lang=").append(URLEncoder.encode("en", StandardCharsets.UTF_8));
//         requestBody.append("&models=").append(URLEncoder.encode("general,self-harm", StandardCharsets.UTF_8));
//         requestBody.append("&mode=").append(URLEncoder.encode("ml", StandardCharsets.UTF_8));
//         requestBody.append("&api_user=").append(URLEncoder.encode(API_USER, StandardCharsets.UTF_8));
//         requestBody.append("&api_secret=").append(URLEncoder.encode(API_SECRET, StandardCharsets.UTF_8));
        
//         HttpRequest request = HttpRequest.newBuilder()
//                 .uri(URI.create(TEXT_MODERATION_URL))
//                 .header("Content-Type", "application/x-www-form-urlencoded")
//                 .POST(HttpRequest.BodyPublishers.ofString(requestBody.toString()))
//                 .build();
                
//         HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
//         Map<String, Object> result = objectMapper.readValue(response.body(), Map.class);
        
//         boolean safe = true;
//         List<String> flaggedCategories = new ArrayList<>();
        
//         // Check the "moderation_classes" object for each category score (except "available")
//         if(result.containsKey("moderation_classes")) {
//             Object modClassesObj = result.get("moderation_classes");
//             if (modClassesObj instanceof Map) {
//                 Map<String, Object> modClasses = (Map<String, Object>) modClassesObj;
//                 for (Map.Entry<String, Object> entry : modClasses.entrySet()) {
//                     String key = entry.getKey();
//                     if ("available".equals(key)) continue;
//                     double score = 0.0;
//                     try {
//                         score = ((Number) entry.getValue()).doubleValue();
//                     } catch (Exception e) {
//                         continue;
//                     }
//                     if(score >= TEXT_THRESHOLD) {
//                         safe = false;
//                         flaggedCategories.add(key);
//                     }
//                 }
//             }
//         }
        
//         result.put("safe", safe);
//         if (!safe) {
//             result.put("flaggedCategories", String.join(" ", flaggedCategories));
//         }
        
//         return result;
//     }
    
//     /**
//      * Moderates an image by converting the file to a data URL and sending a GET request
//      * to Sightengine’s image endpoint with the required models. The response is analyzed
//      * by checking the "nudity" category (using the "none" score) and a set of other sensitive categories.
//      * It returns a map containing a Boolean "safe" flag and, if unsafe, a list of flagged categories.
//      */
//     public Map<String, Object> moderateImage(MultipartFile file) throws Exception {
//         byte[] imageBytes = file.getBytes();
//         String base64 = Base64.getEncoder().encodeToString(imageBytes);
//         String dataUrl = "data:" + file.getContentType() + ";base64," + base64;
        
//         String modelsParam = "nudity-2.1,weapon,alcohol,recreational_drug,medical,offensive-2.0,scam,text-content,gore-2.0,qr-content,tobacco,violence,self-harm,money,gambling";
//         String urlWithParams = IMAGE_MODERATION_URL + "?" +
//                 "url=" + URLEncoder.encode(dataUrl, StandardCharsets.UTF_8) +
//                 "&models=" + URLEncoder.encode(modelsParam, StandardCharsets.UTF_8) +
//                 "&api_user=" + URLEncoder.encode(API_USER, StandardCharsets.UTF_8) +
//                 "&api_secret=" + URLEncoder.encode(API_SECRET, StandardCharsets.UTF_8);
                
//         HttpRequest request = HttpRequest.newBuilder()
//                 .uri(URI.create(urlWithParams))
//                 .GET()
//                 .build();
                
//         HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
//         Map<String, Object> result = objectMapper.readValue(response.body(), Map.class);
        
//         boolean safe = true;
//         List<String> flaggedCategories = new ArrayList<>();
        
//         // Evaluate the "nudity" category: ensure the "none" score is high
//         if(result.containsKey("nudity") && result.get("nudity") instanceof Map) {
//             Map<String, Object> nudity = (Map<String, Object>) result.get("nudity");
//             if(nudity.containsKey("none")) {
//                 double noneScore = ((Number) nudity.get("none")).doubleValue();
//                 if(noneScore < IMAGE_NONE_THRESHOLD) {
//                     safe = false;
//                     flaggedCategories.add("nudity");
//                 }
//             }
//             // Also check if any other subcategory under nudity exceeds the general threshold
//             for (Map.Entry<String, Object> entry : nudity.entrySet()) {
//                 String key = entry.getKey();
//                 if("none".equals(key)) continue;
//                 try {
//                     double score = ((Number) entry.getValue()).doubleValue();
//                     if(score >= IMAGE_GENERAL_THRESHOLD) {
//                         safe = false;
//                         if(!flaggedCategories.contains("nudity"))
//                             flaggedCategories.add("nudity");
//                         break;
//                     }
//                 } catch(Exception e) {
//                     // Skip non-numeric entries
//                 }
//             }
//         }
        
//         // List of other sensitive categories to evaluate
//         String[] categories = {"weapon", "recreational_drug", "medical", "alcohol", "offensive", "scam", "gore", "violence", "self-harm", "money", "gambling"};
//         for(String category : categories) {
//             if(result.containsKey(category)) {
//                 Object catObj = result.get(category);
//                 if(catObj instanceof Map) {
//                     Map<String, Object> catMap = (Map<String, Object>) catObj;
//                     // If the category has a "prob" field, check it
//                     if(catMap.containsKey("prob")) {
//                         double prob = ((Number) catMap.get("prob")).doubleValue();
//                         if(prob >= IMAGE_GENERAL_THRESHOLD) {
//                             safe = false;
//                             flaggedCategories.add(category);
//                         }
//                     } else if(catMap.containsKey("classes")) {
//                         // For categories like "weapon" that have nested "classes"
//                         Map<String, Object> classes = (Map<String, Object>) catMap.get("classes");
//                         double maxScore = 0.0;
//                         for (Object value : classes.values()) {
//                             try {
//                                 double score = ((Number) value).doubleValue();
//                                 if(score > maxScore) {
//                                     maxScore = score;
//                                 }
//                             } catch(Exception e) { }
//                         }
//                         if(maxScore >= IMAGE_GENERAL_THRESHOLD) {
//                             safe = false;
//                             flaggedCategories.add(category);
//                         }
//                     }
//                 }
//             }
//         }
        
//         result.put("safe", safe);
//         if (!safe) {
//             result.put("flaggedCategories", String.join(" ", flaggedCategories));
//         }
        
//         return result;
//     }
// }
