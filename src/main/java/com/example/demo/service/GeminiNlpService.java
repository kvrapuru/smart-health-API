package com.example.demo.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.example.demo.model.FoodItem;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

@Service
public class GeminiNlpService {
    
    private static final Logger logger = LoggerFactory.getLogger(GeminiNlpService.class);
    private final String GEMINI_API_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent";
    
    @Value("${gemini.api.key}")
    private String apiKey;

    public String getApiKey() {
        return apiKey;
    }

    public String testGeminiConnection() {
        try {
            String prompt = "Say hello in one word";
            
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            String payload = "{ \"contents\": [{ \"parts\": [{ \"text\": \"" + prompt + "\" }] }] }";
            HttpEntity<String> entity = new HttpEntity<>(payload, headers);
            String url = GEMINI_API_URL + "?key=" + apiKey;

            logger.info("Testing Gemini API connection...");
            ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);
            logger.info("Gemini API test response status: {}", response.getStatusCode());
            logger.info("Gemini API test response body: {}", response.getBody());

            ObjectMapper mapper = new ObjectMapper();
            JsonNode node = mapper.readTree(response.getBody());
            String output = node.get("candidates").get(0).get("content").get("parts").get(0).get("text").asText();
            
            return "Success: " + output;
        } catch (Exception e) {
            logger.error("Error testing Gemini API: {}", e.getMessage(), e);
            return "Error: " + e.getMessage();
        }
    }

    public List<FoodItem> parseInputWithGemini(String inputText) {
        try {
            logger.info("Processing input: {}", inputText);
            logger.info("Using API key: {}", apiKey.substring(0, 10) + "...");
            
            String prompt = "Extract food items, quantities, and nutrition information from this text and respond with a valid JSON array only (no markdown formatting, no code blocks). " +
                    "Each item should have name, quantity, unit, and nutrition fields (calories, protein, carbohydrates, fat, fiber, sugar, sodium). " +
                    "Provide realistic nutrition values per the specified quantity. " +
                    "Example format: [{\"name\": \"banana\", \"quantity\": 1, \"unit\": \"piece\", \"calories\": 105, \"protein\": 1.3, \"carbohydrates\": 27, \"fat\": 0.4, \"fiber\": 3.1, \"sugar\": 14, \"sodium\": 1}, {\"name\": \"chicken rice\", \"quantity\": 200, \"unit\": \"grams\", \"calories\": 260, \"protein\": 8, \"carbohydrates\": 45, \"fat\": 4, \"fiber\": 2, \"sugar\": 1, \"sodium\": 400}] " +
                    "Input text: " + inputText;

            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            ObjectMapper mapper = new ObjectMapper();
            ObjectNode part = mapper.createObjectNode();
            part.put("text", prompt);

            ArrayNode partsArray = mapper.createArrayNode();
            partsArray.add(part);
            ObjectNode parts = mapper.createObjectNode();
            parts.set("parts", partsArray);

            ArrayNode contentsArray = mapper.createArrayNode();
            contentsArray.add(parts);
            ObjectNode contents = mapper.createObjectNode();
            contents.set("contents", contentsArray);

            String payload = mapper.writeValueAsString(contents);

            HttpEntity<String> entity = new HttpEntity<>(payload, headers);
            String url = GEMINI_API_URL + "?key=" + apiKey;

            logger.info("Calling Gemini API...");
            ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);
            logger.info("Gemini API response status: {}", response.getStatusCode());
            logger.info("Gemini API response body: {}", response.getBody());
            
            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                try {
                    JsonNode rootNode = mapper.readTree(response.getBody());
                    
                    // Log the full response structure for debugging
                    logger.info("Full Gemini response: {}", mapper.writerWithDefaultPrettyPrinter().writeValueAsString(rootNode));
                    
                    JsonNode candidates = rootNode.path("candidates");
                    if (candidates.isArray() && candidates.size() > 0) {
                        JsonNode content = candidates.get(0).path("content");
                        JsonNode responseParts = content.path("parts");
                        if (responseParts.isArray() && responseParts.size() > 0) {
                            String text = responseParts.get(0).path("text").asText();
                            logger.info("Extracted text from Gemini: {}", text);
                            
                            // Clean the text - remove markdown code blocks if present
                            String cleanedText = text.trim();
                            if (cleanedText.startsWith("```json")) {
                                cleanedText = cleanedText.substring(7); // Remove ```json
                            }
                            if (cleanedText.startsWith("```")) {
                                cleanedText = cleanedText.substring(3); // Remove ```
                            }
                            if (cleanedText.endsWith("```")) {
                                cleanedText = cleanedText.substring(0, cleanedText.length() - 3); // Remove trailing ```
                            }
                            cleanedText = cleanedText.trim();
                            
                            logger.info("Cleaned text: {}", cleanedText);
                            
                            // Try to parse the text as JSON
                            try {
                                List<FoodItem> foodItems = mapper.readValue(cleanedText, new TypeReference<List<FoodItem>>() {});
                                logger.info("Successfully parsed {} food items", foodItems.size());
                                return foodItems;
                            } catch (Exception e) {
                                logger.error("Failed to parse text as JSON: {}", cleanedText, e);
                                return new ArrayList<>();
                            }
                        }
                    }
                } catch (Exception e) {
                    logger.error("Error parsing Gemini response", e);
                }
            }
            
            return new ArrayList<>();
        } catch (Exception e) {
            logger.error("Error processing input with Gemini: {}", e.getMessage(), e);
            return new ArrayList<>();
        }
    }

    public String getRawGeminiResponse(String inputText) {
        try {
            String prompt = "Extract food items, quantities, and nutrition information from this text and respond with a valid JSON array only (no markdown formatting, no code blocks). " +
                    "Each item should have name, quantity, unit, and nutrition fields (calories, protein, carbohydrates, fat, fiber, sugar, sodium). " +
                    "Provide realistic nutrition values per the specified quantity. " +
                    "Example format: [{\"name\": \"banana\", \"quantity\": 1, \"unit\": \"piece\", \"calories\": 105, \"protein\": 1.3, \"carbohydrates\": 27, \"fat\": 0.4, \"fiber\": 3.1, \"sugar\": 14, \"sodium\": 1}, {\"name\": \"chicken rice\", \"quantity\": 200, \"unit\": \"grams\", \"calories\": 260, \"protein\": 8, \"carbohydrates\": 45, \"fat\": 4, \"fiber\": 2, \"sugar\": 1, \"sodium\": 400}] " +
                    "Input text: " + inputText;

            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            ObjectMapper mapper = new ObjectMapper();
            ObjectNode part = mapper.createObjectNode();
            part.put("text", prompt);

            ArrayNode partsArray = mapper.createArrayNode();
            partsArray.add(part);
            ObjectNode parts = mapper.createObjectNode();
            parts.set("parts", partsArray);

            ArrayNode contentsArray = mapper.createArrayNode();
            contentsArray.add(parts);
            ObjectNode contents = mapper.createObjectNode();
            contents.set("contents", contentsArray);

            String payload = mapper.writeValueAsString(contents);

            HttpEntity<String> entity = new HttpEntity<>(payload, headers);
            String url = GEMINI_API_URL + "?key=" + apiKey;

            ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);
            return response.getBody();
        } catch (Exception e) {
            logger.error("Error getting raw Gemini response: {}", e.getMessage(), e);
            return "Error: " + e.getMessage();
        }
    }
}
