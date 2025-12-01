package edu.uic.marketplace.repository.notification;

import edu.uic.marketplace.model.notification.Notification;
import edu.uic.marketplace.model.notification.NotificationType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    /**
     * Prevents N+1 query when loading notifications
     */
    @EntityGraph(attributePaths = {"user"})
    @Query("""
           SELECT n FROM Notification n
           WHERE n.user.username = :username
           ORDER BY n.createdAt DESC
           """)
    Page<Notification> findByUser_UsernameOrderByCreatedAtDescOptimized(
            @Param("username") String username,
            Pageable pageable
    );

    /**
     * Original method (kept for backward compatibility)
     */
    Page<Notification> findByUser_UsernameOrderByCreatedAtDesc(String username, Pageable pageable);

    @EntityGraph(attributePaths = {"user"})
    @Query("""
           SELECT n FROM Notification n
           WHERE n.user.username = :username
             AND n.readAt IS NULL
           ORDER BY n.createdAt DESC
           """)
    Page<Notification> findByUser_UsernameAndReadAtIsNullOrderByCreatedAtDescOptimized(
            @Param("username") String username,
            Pageable pageable
    );

    /**
     * Original method (kept for backward compatibility)
     */
    Page<Notification> findByUser_UsernameAndReadAtIsNullOrderByCreatedAtDesc(String username, Pageable pageable);

    /**
     * Find notifications for a user filtered by type.
     */
    List<Notification> findByUser_UsernameAndType(String username, NotificationType type);

    /**
     * Count unread notifications for a user.
     */
    Long countByUser_UsernameAndReadAtIsNull(String username);

    /**
     * Mark all unread notifications for a user as read.
     */
    @Modifying
    @Query("""
        UPDATE Notification n
           SET n.readAt = CURRENT_TIMESTAMP
         WHERE n.user.username = :username
           AND n.readAt IS NULL
        """)
    void markAllAsReadByUsername(@Param("username") String username);

    /**
     * Delete all notifications for a user.
     */
    void deleteByUser_Username(String username);

    /**
     * Delete notifications by related entity.
     */
    void deleteByEntityTypeAndEntityId(String entityType, String entityId);


    /**
     * Find notification by publicId and username (ownership check).
     */
    Optional<Notification> findByPublicIdAndUser_Username(String publicId, String username);

    /**
     * Find notification by publicId.
     */
    Optional<Notification> findByPublicId(String publicId);
}
