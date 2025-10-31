package edu.uic.marketplace.service.search;

import edu.uic.marketplace.dto.request.search.SaveSearchRequest;
import edu.uic.marketplace.dto.response.search.SavedSearchResponse;
import edu.uic.marketplace.model.search.SavedSearch;
import edu.uic.marketplace.model.user.User;
import edu.uic.marketplace.repository.search.SavedSearchRepository;
import edu.uic.marketplace.service.user.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("SavedSearchService Unit Test")
class SavedSearchServiceTest {

    @Mock
    private SavedSearchRepository savedSearchRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private SavedSearchServiceImpl savedSearchService;

    private User testUser;
    private SavedSearch savedSearch;

    @BeforeEach
    void setUp() {

        testUser = User.builder()
                .userId(1L)
                .email("user@uic.edu")
                .build();

        savedSearch = SavedSearch.builder()
                .savedSearchId(1L)
                .user(testUser)
                .name("CS Textbooks")
                .query("computer science textbook")
                .filters("{\"category\":\"books\"}")
                .createdAt(Instant.now())
                .build();
    }

    @Test
    @DisplayName("Save search - Success")
    void saveSearch_Success() {

        // Given
        SaveSearchRequest request = SaveSearchRequest.builder()
                .name("CS Textbooks")
                .query("computer science textbook")
                .filters("{\"category\":\"books\"}")
                .build();

        when(userService.findById(1L)).thenReturn(Optional.of(testUser));
        when(savedSearchRepository.save(any(SavedSearch.class))).thenReturn(savedSearch);

        // When
        SavedSearchResponse response = savedSearchService.saveSearch(1L, request);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getName()).isEqualTo("CS Textbooks");
        assertThat(response.getQuery()).isEqualTo("computer science textbook");
        verify(savedSearchRepository, times(1)).save(any(SavedSearch.class));
    }

    @Test
    @DisplayName("Find searches by id")
    void findById_Success() {

        // Given
        when(savedSearchRepository.findById(1L)).thenReturn(Optional.of(savedSearch));

        // When
        Optional<SavedSearch> result = savedSearchService.findById(1L);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getName()).isEqualTo("CS Textbooks");
        verify(savedSearchRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Get user's saved searches")
    void getUserSavedSearches() {

        // Given
        SavedSearch search2 = SavedSearch.builder()
                .savedSearchId(2L)
                .user(testUser)
                .name("Math Books")
                .query("mathematics")
                .createdAt(Instant.now())
                .build();

        when(savedSearchRepository.findByUser_UserId(1L))
                .thenReturn(Arrays.asList(savedSearch, search2));

        // When
        List<SavedSearchResponse> results = savedSearchService.getUserSavedSearches(1L);

        // Then
        assertThat(results).hasSize(2);
        assertThat(results).extracting("name")
                .containsExactly("CS Textbooks", "Math Books");
        verify(savedSearchRepository, times(1)).findByUser_UserId(1L);
    }

    @Test
    @DisplayName("Delete saved search")
    void deleteSavedSearch() {
        // Given
        when(savedSearchRepository.findById(1L)).thenReturn(Optional.of(savedSearch));

        // When
        savedSearchService.deleteSavedSearch(1L, 1L);

        // Then
        verify(savedSearchRepository, times(1)).findById(1L);
        verify(savedSearchRepository, times(1)).delete(savedSearch);
    }

    @Test
    @DisplayName("Delete saved search - Unauthorized")
    void deleteSavedSearch_Unauthorized() {
        // Given
        when(savedSearchRepository.findById(1L)).thenReturn(Optional.of(savedSearch));

        // When & Then
        assertThatThrownBy(() -> savedSearchService.deleteSavedSearch(1L, 999L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Unauthorized");

        verify(savedSearchRepository, times(1)).findById(1L);
        verify(savedSearchRepository, never()).delete(any());
    }

    @Test
    @DisplayName("Update search name")
    void updateSearchName() {
        // Given
        when(savedSearchRepository.findById(1L)).thenReturn(Optional.of(savedSearch));
        when(savedSearchRepository.save(any(SavedSearch.class))).thenReturn(savedSearch);

        // When
        SavedSearchResponse response = savedSearchService.updateSearchName(1L, 1L, "Updated Name");

        // Then
        assertThat(savedSearch.getName()).isEqualTo("Updated Name");
        verify(savedSearchRepository, times(1)).save(savedSearch);
    }

    @Test
    @DisplayName("Check for duplicate search - Exists")
    void hasSavedSearch_True() {
        // Given
        String queryHash = "hash123";
        when(savedSearchRepository.existsByUser_UserIdAndQueryHash(1L, queryHash))
                .thenReturn(true);

        // When
        boolean result = savedSearchService.hasSavedSearch(1L, queryHash);

        // Then
        assertThat(result).isTrue();
        verify(savedSearchRepository, times(1))
                .existsByUser_UserIdAndQueryHash(1L, queryHash);
    }

    @Test
    @DisplayName("Check for duplicate search - Does not exist")
    void hasSavedSearch_False() {
        // Given
        String queryHash = "hash456";
        when(savedSearchRepository.existsByUser_UserIdAndQueryHash(1L, queryHash))
                .thenReturn(false);

        // When
        boolean result = savedSearchService.hasSavedSearch(1L, queryHash);

        // Then
        assertThat(result).isFalse();
        verify(savedSearchRepository, times(1))
                .existsByUser_UserIdAndQueryHash(1L, queryHash);
    }

    @Test
    @DisplayName("Find search by ID - Not found")
    void findById_NotFound() {
        // Given
        when(savedSearchRepository.findById(999L)).thenReturn(Optional.empty());

        // When
        Optional<SavedSearch> result = savedSearchService.findById(999L);

        // Then
        assertThat(result).isEmpty();
        verify(savedSearchRepository, times(1)).findById(999L);
    }
}
