package com.skillgap.ai.vector;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Component
public class QdrantClient {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final String url;
    private final String apiKey;
    private final String collectionName = "learning_resources";

    public QdrantClient(RestTemplate restTemplate,
                        ObjectMapper objectMapper,
                        @Value("${qdrant.url}") String url,
                        @Value("${qdrant.api.key}") String apiKey) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
        this.url = url;
        this.apiKey = apiKey;
    }

    private HttpHeaders buildHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        if (apiKey != null && !apiKey.trim().isEmpty()) {
            headers.set("api-key", apiKey);
        }
        return headers;
    }

    public void initializeCollection() {
        String checkUrl = url + "/collections/" + collectionName;
        HttpHeaders headers = buildHeaders();

        try {
            HttpEntity<Void> entity = new HttpEntity<>(headers);
            ResponseEntity<String> response = restTemplate.exchange(checkUrl, HttpMethod.GET, entity, String.class);
            // Collection exists, no action required
            return;
        } catch (HttpClientErrorException.NotFound e) {
            // Collection doesn't exist, create it
            createCollection();
        } catch (Exception e) {
            // Log warning or throw depending on environment preference
            System.err.println("Could not reach Qdrant on initialize: " + e.getMessage());
        }
    }

    private void createCollection() {
        String createUrl = url + "/collections/" + collectionName;
        HttpHeaders headers = buildHeaders();

        Map<String, Object> body = new HashMap<>();
        Map<String, Object> vectors = new HashMap<>();
        vectors.put("size", 768); // Matching Gemini text-embedding-004
        vectors.put("distance", "Cosine");
        body.put("vectors", vectors);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);
        try {
            restTemplate.exchange(createUrl, HttpMethod.PUT, entity, String.class);
            System.out.println("Created Qdrant collection: " + collectionName);
        } catch (Exception e) {
            throw new RuntimeException("Failed to create Qdrant collection: " + e.getMessage(), e);
        }
    }

    public void upsertPoint(Long resourceId, List<Double> vector, Map<String, Object> payload) {
        String upsertUrl = url + "/collections/" + collectionName + "/points?wait=true";
        HttpHeaders headers = buildHeaders();

        Map<String, Object> point = new HashMap<>();
        point.put("id", resourceId);
        point.put("vector", vector);
        point.put("payload", payload);

        Map<String, Object> body = new HashMap<>();
        body.put("points", Collections.singletonList(point));

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);
        try {
            restTemplate.exchange(upsertUrl, HttpMethod.PUT, entity, String.class);
        } catch (Exception e) {
            System.err.println("Failed to upsert point to Qdrant: " + e.getMessage());
        }
    }

    public List<Long> search(List<Double> vector, int limit) {
        String searchUrl = url + "/collections/" + collectionName + "/points/search";
        HttpHeaders headers = buildHeaders();

        Map<String, Object> body = new HashMap<>();
        body.put("vector", vector);
        body.put("limit", limit);
        body.put("with_payload", true);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);
        List<Long> resourceIds = new ArrayList<>();

        try {
            ResponseEntity<String> response = restTemplate.exchange(searchUrl, HttpMethod.POST, entity, String.class);
            JsonNode rootNode = objectMapper.readTree(response.getBody());
            JsonNode resultNode = rootNode.path("result");

            if (resultNode.isArray()) {
                for (JsonNode node : resultNode) {
                    JsonNode payloadNode = node.path("payload");
                    if (payloadNode.has("resourceId")) {
                        resourceIds.add(payloadNode.get("resourceId").asLong());
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Qdrant search failed: " + e.getMessage());
        }

        return resourceIds;
    }
}
