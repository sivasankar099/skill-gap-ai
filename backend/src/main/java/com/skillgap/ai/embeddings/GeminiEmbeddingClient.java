package com.skillgap.ai.embeddings;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Component
public class GeminiEmbeddingClient {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final String apiKey;
    private final String apiUrl;

    public GeminiEmbeddingClient(RestTemplate restTemplate,
                                 ObjectMapper objectMapper,
                                 @Value("${GEMINI_API_KEY:${gemini.api.key:}}") String apiKey,
                                 @Value("${gemini.api.url}") String apiUrl) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
        this.apiKey = apiKey;
        this.apiUrl = apiUrl;
    }

    public List<Double> embed(String text) {
        if (apiKey == null || apiKey.trim().isEmpty()) {
            throw new IllegalStateException("Gemini API key is not configured.");
        }

        String endpointUrl = apiUrl + "/gemini-embedding-001:embedContent?key=" + apiKey;

        try {
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", "models/text-embedding-004");

            Map<String, Object> textPart = new HashMap<>();
            textPart.put("text", text);

            Map<String, Object> contentNode = new HashMap<>();
            contentNode.put("parts", Collections.singletonList(textPart));
            requestBody.put("content", contentNode);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);

            String responseStr = restTemplate.postForObject(endpointUrl, requestEntity, String.class);

            JsonNode rootNode = objectMapper.readTree(responseStr);
            JsonNode valuesNode = rootNode.path("embedding").path("values");

            List<Double> vector = new ArrayList<>();
            if (valuesNode.isArray()) {
                for (JsonNode val : valuesNode) {
                    vector.add(val.asDouble());
                }
            }

            return vector;
        } catch (Exception ex) {
            throw new RuntimeException("Failed to generate embedding: " + ex.getMessage(), ex);
        }
    }
}
