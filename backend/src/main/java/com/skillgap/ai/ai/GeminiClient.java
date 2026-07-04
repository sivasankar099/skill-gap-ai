package com.skillgap.ai.ai;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.skillgap.ai.dto.ResumeExtractionResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Component
public class GeminiClient {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final String apiKey;
    private final String apiUrl;

    public GeminiClient(RestTemplate restTemplate,
                        ObjectMapper objectMapper,
                        @Value("${gemini.api.key}") String apiKey,
                        @Value("${gemini.api.url}") String apiUrl) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
        this.apiKey = apiKey;
        this.apiUrl = apiUrl;
    }

    public ResumeExtractionResponse extractResumeData(String resumeText) {
        if (apiKey == null || apiKey.trim().isEmpty()) {
            throw new IllegalStateException("Gemini API key is not configured. Please set the GEMINI_API_KEY environment variable.");
        }

        String endpointUrl = apiUrl + "/gemini-2.5-flash:generateContent?key=" + apiKey;

        try {
            // Build the payload
            Map<String, Object> requestBody = new HashMap<>();

            // Contents list containing parts
            Map<String, Object> textPart = new HashMap<>();
            textPart.put("text", "Extract professional skills, education, experience, and projects from the following resume text. " +
                    "Return ONLY the requested JSON. Make sure technicalSkills are normalized/standardized (e.g. use standard spellings like 'Java', 'React JS', 'Spring Boot', 'MySQL', 'Python', 'SQL', etc. to match standard role requirements).\n\nResume Text:\n" + resumeText);

            Map<String, Object> partContainer = new HashMap<>();
            partContainer.put("parts", Collections.singletonList(textPart));
            requestBody.put("contents", Collections.singletonList(partContainer));

            // Generation config with strict schema definition
            Map<String, Object> generationConfig = new HashMap<>();
            generationConfig.put("responseMimeType", "application/json");

            Map<String, Object> schema = new HashMap<>();
            schema.put("type", "OBJECT");

            Map<String, Object> properties = new HashMap<>();

            // technicalSkills Array schema
            Map<String, Object> technicalSkillsSchema = new HashMap<>();
            technicalSkillsSchema.put("type", "ARRAY");
            technicalSkillsSchema.put("items", Collections.singletonMap("type", "STRING"));
            properties.put("technicalSkills", technicalSkillsSchema);

            // softSkills Array schema
            Map<String, Object> softSkillsSchema = new HashMap<>();
            softSkillsSchema.put("type", "ARRAY");
            softSkillsSchema.put("items", Collections.singletonMap("type", "STRING"));
            properties.put("softSkills", softSkillsSchema);

            // projects Array schema
            Map<String, Object> projectsSchema = new HashMap<>();
            projectsSchema.put("type", "ARRAY");
            Map<String, Object> projectItems = new HashMap<>();
            projectItems.put("type", "OBJECT");
            Map<String, Object> projectProperties = new HashMap<>();
            projectProperties.put("name", Collections.singletonMap("type", "STRING"));
            projectProperties.put("description", Collections.singletonMap("type", "STRING"));
            projectItems.put("properties", projectProperties);
            projectItems.put("required", Arrays.asList("name", "description"));
            projectsSchema.put("items", projectItems);
            properties.put("projects", projectsSchema);

            // experience Array schema
            Map<String, Object> experienceSchema = new HashMap<>();
            experienceSchema.put("type", "ARRAY");
            experienceSchema.put("items", Collections.singletonMap("type", "STRING"));
            properties.put("experience", experienceSchema);

            // education Array schema
            Map<String, Object> educationSchema = new HashMap<>();
            educationSchema.put("type", "ARRAY");
            educationSchema.put("items", Collections.singletonMap("type", "STRING"));
            properties.put("education", educationSchema);

            schema.put("properties", properties);
            schema.put("required", Arrays.asList("technicalSkills", "softSkills", "projects", "experience", "education"));
            generationConfig.put("responseSchema", schema);

            requestBody.put("generationConfig", generationConfig);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);

            // Execute POST request
            String responseStr = restTemplate.postForObject(endpointUrl, requestEntity, String.class);

            // Read candidate content
            JsonNode rootNode = objectMapper.readTree(responseStr);
            JsonNode textNode = rootNode.path("candidates").get(0)
                    .path("content").path("parts").get(0).path("text");

            String jsonText = textNode.asText();

            // Deserialize to DTO
            return objectMapper.readValue(jsonText, ResumeExtractionResponse.class);

        } catch (Exception ex) {
            throw new RuntimeException("Failed to extract skills using Gemini: " + ex.getMessage(), ex);
        }
    }

    public String generateRoadmapAndExplanation(String targetRoleName, String missingSkillsJson, String strongSkillsJson) {
        if (apiKey == null || apiKey.trim().isEmpty()) {
            throw new IllegalStateException("Gemini API key is not configured.");
        }

        String endpointUrl = apiUrl + "/gemini-2.5-flash:generateContent?key=" + apiKey;

        try {
            String promptText = String.format(
                    "You are a Senior Career Mentor. Provide a brief 2-3 paragraph gap analysis explanation and a structured 4-week learning roadmap for a candidate aiming to become a '%s'.\n" +
                    "They possess these strong skills: %s\n" +
                    "But they are missing these critical skills: %s\n" +
                    "Generate a response in JSON format matching this exact schema:\n" +
                    "{\n" +
                    "  \"gapExplanation\": \"Brief explanation of where they stand and what key areas they need to focus on.\",\n" +
                    "  \"weeks\": [\n" +
                    "    {\n" +
                    "      \"weekNumber\": 1,\n" +
                    "      \"focus\": \"Main topic of week 1\",\n" +
                    "      \"topics\": [\"Topic A\", \"Topic B\"],\n" +
                    "      \"outcome\": \"What they can build or do by end of week\"\n" +
                    "    },\n" +
                    "    ...\n" +
                    "  ]\n" +
                    "}\n" +
                    "Return ONLY the raw JSON string.",
                    targetRoleName, strongSkillsJson, missingSkillsJson
            );

            Map<String, Object> requestBody = new HashMap<>();
            Map<String, Object> textPart = new HashMap<>();
            textPart.put("text", promptText);
            Map<String, Object> partContainer = new HashMap<>();
            partContainer.put("parts", Collections.singletonList(textPart));
            requestBody.put("contents", Collections.singletonList(partContainer));

            Map<String, Object> generationConfig = new HashMap<>();
            generationConfig.put("responseMimeType", "application/json");

            // Define schema
            Map<String, Object> schema = new HashMap<>();
            schema.put("type", "OBJECT");
            Map<String, Object> properties = new HashMap<>();
            properties.put("gapExplanation", Collections.singletonMap("type", "STRING"));

            Map<String, Object> weeksSchema = new HashMap<>();
            weeksSchema.put("type", "ARRAY");
            Map<String, Object> weekItemSchema = new HashMap<>();
            weekItemSchema.put("type", "OBJECT");
            Map<String, Object> weekProps = new HashMap<>();
            weekProps.put("weekNumber", Collections.singletonMap("type", "INTEGER"));
            weekProps.put("focus", Collections.singletonMap("type", "STRING"));
            weekProps.put("topics", Collections.singletonMap("type", "ARRAY"));
            ((Map<String, Object>)weekProps.get("topics")).put("items", Collections.singletonMap("type", "STRING"));
            weekProps.put("outcome", Collections.singletonMap("type", "STRING"));
            weekItemSchema.put("properties", weekProps);
            weekItemSchema.put("required", Arrays.asList("weekNumber", "focus", "topics", "outcome"));
            weeksSchema.put("items", weekItemSchema);

            properties.put("weeks", weeksSchema);
            schema.put("properties", properties);
            schema.put("required", Arrays.asList("gapExplanation", "weeks"));
            generationConfig.put("responseSchema", schema);
            requestBody.put("generationConfig", generationConfig);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);

            String responseStr = restTemplate.postForObject(endpointUrl, requestEntity, String.class);

            JsonNode rootNode = objectMapper.readTree(responseStr);
            return rootNode.path("candidates").get(0)
                    .path("content").path("parts").get(0).path("text").asText();

        } catch (Exception ex) {
            throw new RuntimeException("Failed to generate career roadmap using Gemini: " + ex.getMessage(), ex);
        }
    }
}
