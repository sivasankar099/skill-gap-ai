package com.skillgap.ai.service;

import com.skillgap.ai.embeddings.GeminiEmbeddingClient;
import com.skillgap.ai.entity.LearningResource;
import com.skillgap.ai.repository.LearningResourceRepository;
import com.skillgap.ai.vector.QdrantClient;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class RecommendationService {

    private final QdrantClient qdrantClient;
    private final GeminiEmbeddingClient embeddingClient;
    private final LearningResourceRepository learningResourceRepository;

    public RecommendationService(QdrantClient qdrantClient,
                                 GeminiEmbeddingClient embeddingClient,
                                 LearningResourceRepository learningResourceRepository) {
        this.qdrantClient = qdrantClient;
        this.embeddingClient = embeddingClient;
        this.learningResourceRepository = learningResourceRepository;
    }

    public List<LearningResource> getRecommendations(List<String> missingSkills) {
        if (missingSkills == null || missingSkills.isEmpty()) {
            return Collections.emptyList();
        }

        Set<Long> matchedIds = new HashSet<>();

        // Generate embeddings for each missing skill and query Qdrant
        for (String skill : missingSkills) {
            try {
                // Vectorize missing skill name
                List<Double> vector = embeddingClient.embed(skill);
                
                // Query Qdrant for top 3 matching learning resources
                List<Long> searchResultIds = qdrantClient.search(vector, 3);
                matchedIds.addAll(searchResultIds);
            } catch (Exception ex) {
                System.err.println("Failed to perform vector search for skill '" + skill + "': " + ex.getMessage());
            }
        }

        if (matchedIds.isEmpty()) {
            return Collections.emptyList();
        }

        // Fetch detailed resource data from MySQL
        return learningResourceRepository.findByIdIn(new ArrayList<>(matchedIds));
    }
}
