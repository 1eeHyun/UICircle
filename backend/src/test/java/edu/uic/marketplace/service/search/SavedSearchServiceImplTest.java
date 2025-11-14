package edu.uic.marketplace.service.search;

import edu.uic.marketplace.dto.request.search.SaveSearchRequest;
import edu.uic.marketplace.dto.response.search.SavedSearchResponse;
import edu.uic.marketplace.model.search.SavedSearch;
import edu.uic.marketplace.model.user.User;
import edu.uic.marketplace.repository.search.SavedSearchRepository;
import edu.uic.marketplace.validator.auth.AuthValidator;
import edu.uic.marketplace.validator.search.SearchValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SavedSearchServiceImplTest {

    @Mock
    private SavedSearchRepository savedSearchRepository;

    @Mock
    private AuthValidator authValidator;

    @Mock
    private SearchValidator searchValidator;

    private SavedSearchServiceImpl savedSearchService;

    @BeforeEach
    void setUp() {
        savedSearchService = new SavedSearchServiceImpl(
                savedSearchRepository,
                authValidator,
                searchValidator
        );
    }

    // ======================= saveSearch =======================

    @Nested
    @DisplayName("saveSearch")
    class SaveSearchTests {

        @Test
        @DisplayName("should create a new saved search when no existing hash is found")
        void saveSearch_whenNoExistingHash_createsNewEntity() {
            // given
            String username = "john";
            User user = User.builder()
                    .userId(1L)
                    .username(username)
                    .build();

            SaveSearchRequest request = SaveSearchRequest.builder()
                    .name("  Laptop Deals  ")
                    .query(" macbook pro  ")
                    .filters("{\"maxPrice\":1500,\"minPrice\":200}")
                    .build();

            when(authValidator.validateUserByUsername(username)).thenReturn(user);
            when(savedSearchRepository.findByUser_UsernameAndQueryHash(anyString(), anyString()))
                    .thenReturn(Optional.empty());

            SavedSearch saved = SavedSearch.builder()
                    .savedSearchId(10L)
                    .publicId("uuid-1234")
                    .user(user)
                    .name("Laptop Deals")
                    .query("macbook pro")
                    .filters("{\"maxPrice\":1500,\"minPrice\":200}")
                    .queryHash("dummy-hash")
                    .createdAt(Instant.now())
                    .build();

            when(savedSearchRepository.save(any(SavedSearch.class))).thenReturn(saved);

            // when
            SavedSearchResponse response = savedSearchService.saveSearch(username, request);

            // then
            ArgumentCaptor<SavedSearch> captor = ArgumentCaptor.forClass(SavedSearch.class);
            verify(savedSearchRepository).save(captor.capture());
            SavedSearch entityToSave = captor.getValue();

            // normalized values
            assertThat(entityToSave.getUser()).isEqualTo(user);
            assertThat(entityToSave.getName()).isEqualTo("Laptop Deals"); // trimmed
            assertThat(entityToSave.getQuery()).isEqualTo("macbook pro"); // trimmed
            assertThat(entityToSave.getFilters())
                    .isEqualTo("{\"maxPrice\":1500,\"minPrice\":200}");

            // response mapping
            assertThat(response).isNotNull();
            assertThat(response.getPublicId()).isEqualTo(saved.getPublicId());
            assertThat(response.getName()).isEqualTo(saved.getName());
            assertThat(response.getQuery()).isEqualTo(saved.getQuery());
            assertThat(response.getFilters()).isEqualTo(saved.getFilters());
            assertThat(response.getCreatedAt()).isEqualTo(saved.getCreatedAt());
        }

        @Test
        @DisplayName("should update name only when same query+filters already exist")
        void saveSearch_whenExistingHash_updatesNameOnly() {
            // given
            String username = "john";
            User user = User.builder()
                    .userId(1L)
                    .username(username)
                    .build();

            SaveSearchRequest request = SaveSearchRequest.builder()
                    .name("New Name")
                    .query("macbook pro")
                    .filters("{\"minPrice\":200}")
                    .build();

            SavedSearch existing = SavedSearch.builder()
                    .savedSearchId(10L)
                    .publicId("uuid-1234")
                    .user(user)
                    .name("Old Name")
                    .query("macbook pro")
                    .filters("{\"minPrice\":200}")
                    .queryHash("existing-hash")
                    .createdAt(Instant.now())
                    .build();

            when(authValidator.validateUserByUsername(username)).thenReturn(user);
            when(savedSearchRepository.findByUser_UsernameAndQueryHash(eq(username), anyString()))
                    .thenReturn(Optional.of(existing));

            // when
            SavedSearchResponse response = savedSearchService.saveSearch(username, request);

            // then
            // no new entity should be saved
            verify(savedSearchRepository, never()).save(any(SavedSearch.class));

            // existing entity updated
            assertThat(existing.getName()).isEqualTo("New Name");

            // response based on existing entity
            assertThat(response).isNotNull();
            assertThat(response.getPublicId()).isEqualTo(existing.getPublicId());
            assertThat(response.getName()).isEqualTo("New Name");
            assertThat(response.getQuery()).isEqualTo(existing.getQuery());
            assertThat(response.getFilters()).isEqualTo(existing.getFilters());
            assertThat(response.getCreatedAt()).isEqualTo(existing.getCreatedAt());
        }

        @Test
        @DisplayName("should use '{}' when filters are null or blank")
        void saveSearch_whenFiltersNullOrBlank_usesEmptyJson() {
            // given
            String username = "john";
            User user = User.builder()
                    .userId(1L)
                    .username(username)
                    .build();

            SaveSearchRequest request = SaveSearchRequest.builder()
                    .name("Test")
                    .query("test")
                    .filters("   ")
                    .build();

            when(authValidator.validateUserByUsername(username)).thenReturn(user);
            when(savedSearchRepository.findByUser_UsernameAndQueryHash(anyString(), anyString()))
                    .thenReturn(Optional.empty());

            when(savedSearchRepository.save(any(SavedSearch.class))).thenAnswer(invocation -> {
                SavedSearch s = invocation.getArgument(0, SavedSearch.class);
                s.setSavedSearchId(1L);
                s.setPublicId("uuid-1234");
                s.setCreatedAt(Instant.now());
                return s;
            });

            // when
            SavedSearchResponse response = savedSearchService.saveSearch(username, request);

            // then
            ArgumentCaptor<SavedSearch> captor = ArgumentCaptor.forClass(SavedSearch.class);
            verify(savedSearchRepository).save(captor.capture());
            SavedSearch saved = captor.getValue();

            assertThat(saved.getFilters()).isEqualTo("{}");

            assertThat(response).isNotNull();
            assertThat(response.getPublicId()).isEqualTo("uuid-1234");
            assertThat(response.getFilters()).isEqualTo("{}");
        }

        @Test
        @DisplayName("should throw IllegalArgumentException when filters JSON is invalid")
        void saveSearch_whenFiltersInvalid_throwsException() {
            // given
            String username = "john";
            User user = User.builder()
                    .userId(1L)
                    .username(username)
                    .build();

            SaveSearchRequest request = SaveSearchRequest.builder()
                    .name("Test")
                    .query("test")
                    .filters("{invalid-json") // malformed JSON
                    .build();

            when(authValidator.validateUserByUsername(username)).thenReturn(user);

            // when / then
            assertThatThrownBy(() -> savedSearchService.saveSearch(username, request))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("filters must be valid JSON");

            verify(savedSearchRepository, never()).save(any());
        }
    }

    // ======================= getUserSavedSearchesByUsername =======================

    @Nested
    @DisplayName("getUserSavedSearchesByUsername")
    class GetUserSavedSearchesByUsernameTests {

        @Test
        @DisplayName("should return mapped SavedSearchResponse list")
        void getUserSavedSearches_returnsMappedResponses() {
            // given
            String username = "john";

            SavedSearch first = SavedSearch.builder()
                    .savedSearchId(1L)
                    .publicId("uuid-1")
                    .name("First")
                    .query("q1")
                    .filters("{}")
                    .createdAt(Instant.now())
                    .build();

            SavedSearch second = SavedSearch.builder()
                    .savedSearchId(2L)
                    .publicId("uuid-2")
                    .name("Second")
                    .query("q2")
                    .filters("{}")
                    .createdAt(Instant.now().minusSeconds(10))
                    .build();

            when(savedSearchRepository.findByUser_UsernameOrderByCreatedAtDesc(username))
                    .thenReturn(List.of(first, second));

            // when
            List<SavedSearchResponse> result =
                    savedSearchService.getUserSavedSearchesByUsername(username);

            // then
            assertThat(result).hasSize(2);

            SavedSearchResponse r1 = result.get(0);
            SavedSearchResponse r2 = result.get(1);

            assertThat(r1.getPublicId()).isEqualTo("uuid-1");
            assertThat(r1.getName()).isEqualTo("First");
            assertThat(r1.getQuery()).isEqualTo("q1");
            assertThat(r1.getFilters()).isEqualTo("{}");
            assertThat(r1.getCreatedAt()).isEqualTo(first.getCreatedAt());

            assertThat(r2.getPublicId()).isEqualTo("uuid-2");
            assertThat(r2.getName()).isEqualTo("Second");
        }
    }

    // ======================= deleteSavedSearch =======================

    @Nested
    @DisplayName("deleteSavedSearch")
    class DeleteSavedSearchTests {

        @Test
        @DisplayName("should delete saved search when user owns it")
        void deleteSavedSearch_whenOwner_deletesEntity() {
            // given
            String username = "john";

            User user = User.builder()
                    .userId(1L)
                    .username(username)
                    .build();

            SavedSearch entity = SavedSearch.builder()
                    .savedSearchId(10L)
                    .publicId("uuid-1234")
                    .user(user)
                    .build();

            when(searchValidator.validatePublicId("public-id")).thenReturn(entity);

            // when
            savedSearchService.deleteSavedSearch("public-id", username);

            // then
            verify(savedSearchRepository).delete(entity);
        }

        @Test
        @DisplayName("should throw SecurityException when user is not the owner")
        void deleteSavedSearch_whenNotOwner_throwsSecurityException() {
            // given
            String username = "john";

            User otherUser = User.builder()
                    .userId(2L)
                    .username("alice")
                    .build();

            SavedSearch entity = SavedSearch.builder()
                    .savedSearchId(10L)
                    .publicId("uuid-1234")
                    .user(otherUser)
                    .build();

            when(searchValidator.validatePublicId("public-id")).thenReturn(entity);

            // when / then
            assertThatThrownBy(() -> savedSearchService.deleteSavedSearch("public-id", username))
                    .isInstanceOf(SecurityException.class)
                    .hasMessageContaining("You are not allowed");

            verify(savedSearchRepository, never()).delete(any());
        }
    }

    // ======================= deleteAllForUser =======================

    @Nested
    @DisplayName("deleteAllForUser")
    class DeleteAllForUserTests {

        @Test
        @DisplayName("should delete all saved searches for a given username")
        void deleteAllForUser_deletesAll() {
            // given
            String username = "john";

            // when
            savedSearchService.deleteAllForUser(username);

            // then
            verify(savedSearchRepository).deleteByUser_Username(username);
        }
    }

    // ======================= hasSavedSearchByUsername =======================

    @Nested
    @DisplayName("hasSavedSearchByUsername")
    class HasSavedSearchByUsernameTests {

        @Test
        @DisplayName("should return true when repository reports existence")
        void hasSavedSearch_returnsTrueWhenExists() {
            // given
            String username = "john";
            String queryHash = "hash";

            when(savedSearchRepository.existsByUser_UsernameAndQueryHash(username, queryHash))
                    .thenReturn(true);

            // when
            boolean result = savedSearchService.hasSavedSearchByUsername(username, queryHash);

            // then
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("should return false when repository does not find a match")
        void hasSavedSearch_returnsFalseWhenNotExists() {
            // given
            String username = "john";
            String queryHash = "hash";

            when(savedSearchRepository.existsByUser_UsernameAndQueryHash(username, queryHash))
                    .thenReturn(false);

            // when
            boolean result = savedSearchService.hasSavedSearchByUsername(username, queryHash);

            // then
            assertThat(result).isFalse();
        }
    }
}
