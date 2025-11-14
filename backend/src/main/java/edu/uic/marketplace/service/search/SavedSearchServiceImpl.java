package edu.uic.marketplace.service.search;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import edu.uic.marketplace.dto.request.search.SaveSearchRequest;
import edu.uic.marketplace.dto.response.search.SavedSearchResponse;
import edu.uic.marketplace.model.search.SavedSearch;
import edu.uic.marketplace.model.user.User;
import edu.uic.marketplace.repository.search.SavedSearchRepository;
import edu.uic.marketplace.validator.auth.AuthValidator;
import edu.uic.marketplace.validator.search.SearchValidator;
import lombok.RequiredArgsConstructor;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SavedSearchServiceImpl implements SavedSearchService {

    private final SavedSearchRepository savedSearchRepository;
    private final AuthValidator authValidator;
    private final SearchValidator searchValidator;

    // JSON key - ObjectMapper
    private static final ObjectMapper CANONICAL_MAPPER = new ObjectMapper()
            .configure(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS, true);

    @Override
    @Transactional
    public SavedSearchResponse saveSearch(String username, SaveSearchRequest request) {

        // 1) Validation
        User user = authValidator.validateUserByUsername(username);

        // 2) Normalization
        String name = normalizeName(request.getName());
        String query = normalizeQuery(request.getQuery());
        String canonicalFilters = canonicalizeJsonOrThrow(request.getFilters());

        // 3) Create hash (query + filters)
        String queryHash = DigestUtils.sha256Hex(query + "|" + canonicalFilters);

        // 4) check duplication
        // if duplicated entity exists, update name
        // otherwise, save a new entity
        Optional<SavedSearch> existingOpt = savedSearchRepository.findByUser_UsernameAndQueryHash(username, queryHash);

        SavedSearch entity;
        if (existingOpt.isPresent()) {

            entity = existingOpt.get();
            if (!Objects.equals(entity.getName(), name))
                entity.setName(name);

        } else {

            entity = SavedSearch.builder()
                    .user(user)
                    .name(name)
                    .query(query)
                    .createdAt(Instant.now())
                    .filters(canonicalFilters)
                    .queryHash(queryHash)
                    .build();
            entity = savedSearchRepository.save(entity);
        }

        return SavedSearchResponse.from(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SavedSearchResponse> getUserSavedSearchesByUsername(String username) {

        List<SavedSearch> list = savedSearchRepository.findByUser_UsernameOrderByCreatedAtDesc(username);
        return list.stream()
                .map(SavedSearchResponse::from)
                .toList();
    }

    @Override
    @Transactional
    public void deleteSavedSearch(String savedSearchId, String username) {

        SavedSearch entity = searchValidator.validatePublicId(savedSearchId);

        if (!entity.getUser().getUsername().equals(username)) {
            throw new SecurityException("You are not allowed to this action");
        }

        savedSearchRepository.delete(entity);
    }

    @Override
    @Transactional
    public void deleteAllForUser(String username) {
        savedSearchRepository.deleteByUser_Username(username);
    }

    @Override
    public SavedSearchResponse updateSearchName(Long savedSearchId, Long userId, String name) {
        return null;
    }

    @Override
    @Transactional(readOnly = true)
    public boolean hasSavedSearchByUsername(String username, String queryHash) {
        return savedSearchRepository.existsByUser_UsernameAndQueryHash(username, queryHash);
    }


    // ============ utils ============
    private String normalizeName(String name) {
        if (name == null) return null;
        String t = name.trim();
        return t.isEmpty() ? null : t;
    }

    private String normalizeQuery(String query) {
        if (query == null) return "";
        return query.trim();
    }

    /** normalization for filters JSON  */
    private String canonicalizeJsonOrThrow(String raw) {
        try {
            if (raw == null || raw.isBlank()) return "{}";
            JsonNode node = CANONICAL_MAPPER.readTree(raw);
            return CANONICAL_MAPPER.writeValueAsString(node);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("filters must be valid JSON", e);
        }
    }
}
