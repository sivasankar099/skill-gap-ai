package com.skillgap.ai.service;

import com.skillgap.ai.embeddings.GeminiEmbeddingClient;
import com.skillgap.ai.entity.LearningResource;
import com.skillgap.ai.repository.LearningResourceRepository;
import com.skillgap.ai.vector.QdrantClient;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class DatabaseVectorSyncService implements CommandLineRunner {

    private final LearningResourceRepository learningResourceRepository;
    private final QdrantClient qdrantClient;
    private final GeminiEmbeddingClient embeddingClient;

    public DatabaseVectorSyncService(LearningResourceRepository learningResourceRepository,
                                     QdrantClient qdrantClient,
                                     GeminiEmbeddingClient embeddingClient) {
        this.learningResourceRepository = learningResourceRepository;
        this.qdrantClient = qdrantClient;
        this.embeddingClient = embeddingClient;
    }

    @Override
    public void run(String... args) {
        try {
            System.out.println("=== Starting Vector Database Sync ===");
            qdrantClient.initializeCollection();

            List<LearningResource> resources = learningResourceRepository.findAll();
            if (resources.isEmpty()) {
                System.out.println("No learning resources found in MySQL to seed into Qdrant.");
                return;
            }

            System.out.println("Embedding and syncing " + resources.size() + " learning resources to Qdrant...");
            for (LearningResource resource : resources) {
                // Compile textual representation for semantic index
                String textToEmbed = String.format("Title: %s. Description: %s. Skill: %s",
                        resource.getTitle(),
                        resource.getDescription() != null ? resource.getDescription() : "",
                        resource.getSkillName());

                List<Double> vector = embeddingClient.embed(textToEmbed);

                // Prepare payload matching our retrieval contract
                Map<String, Object> payload = new HashMap<>();
                payload.put("resourceId", resource.getId());
                payload.put("title", resource.getTitle());
                payload.put("description", resource.getDescription());
                payload.put("resourceType", resource.getResourceType());
                payload.put("skillName", resource.getSkillName());

                qdrantClient.upsertPoint(resource.getId(), vector, payload);
            }
            System.out.println("=== Qdrant Vector Sync Completed Successfully ===");
        } catch (Exception ex) {
            System.err.println("WARNING: Qdrant vector database sync failed on startup. " +
                    "Verify GEMINI_API_KEY and QDRANT_URL are correct. Root cause: " + ex.getMessage());
        }
    }
}
