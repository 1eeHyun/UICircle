package edu.uic.marketplace.service.moderation;

import edu.uic.marketplace.dto.response.moderation.BlockResponse;
import edu.uic.marketplace.model.moderation.Block;
import edu.uic.marketplace.model.user.User;
import edu.uic.marketplace.repository.moderation.BlockRepository;
import edu.uic.marketplace.service.user.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("BlockService Unit Test")
class BlockServiceTest {

    @Mock
    private BlockRepository blockRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private BlockServiceImpl blockService;

    private User blocker;
    private User blocked;
    private Block block;

    @BeforeEach
    void setUp() {

        blocker = User.builder().userId(1L).build();
        blocked = User.builder().userId(2L).build();
        block = Block.builder()
                .blocker(blocker)
                .blocked(blocked)
                .blockedAt(Instant.now())
                .build();
    }

    @Test
    @DisplayName("Block user - Success")
    void blockUser_Success() {

        // Given
        when(userService.findById(1L)).thenReturn(Optional.of(blocker));
        when(userService.findById(2L)).thenReturn(Optional.of(blocked));
        when(blockRepository.save(any(Block.class))).thenReturn(block);

        // When
        BlockResponse response = blockService.blockUser(1L, 2L);

        // Then
        assertThat(response).isNotNull();
        verify(blockRepository, times(1)).save(any(Block.class));
    }

    @Test
    @DisplayName("Unblock user")
    void unblockUser() {

        // Given
        when(blockRepository.findByBlocker_UserIdAndBlocked_UserId(1L, 2L))
                .thenReturn(Optional.of(block));

        // When
        blockService.unblockUser(1L, 2L);

        // Then
        verify(blockRepository, times(1)).delete(block);
    }

    @Test
    @DisplayName("Is blocked")
    void isBlocked() {

        // Given
        when(blockRepository.existsByBlocker_UserIdAndBlocked_UserId(1L, 2L))
                .thenReturn(true);

        // When
        boolean result = blockService.isBlocked(1L, 2L);

        // Then
        assertThat(result).isTrue();
        verify(blockRepository, times(1)).existsByBlocker_UserIdAndBlocked_UserId(1L, 2L);
    }
}
