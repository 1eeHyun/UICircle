package edu.uic.marketplace.repository.notification;

import edu.uic.marketplace.model.notification.Notification;
import edu.uic.marketplace.model.notification.NotificationType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    
    /**
     * Find notifications by user
     */
    Page<Notification> findByUser_UserId(Long userId, Pageable pageable);
    
    /**
     * Find unread notifications by user
     */
    Page<Notification> findByUser_UserIdAndReadAtIsNull(Long userId, Pageable pageable);
    
    /**
     * Find notifications by user and type
     */
    List<Notification> findByUser_UserIdAndType(Long userId, NotificationType type);
    
    /**
     * Count unread notifications
     */
    Long countByUser_UserIdAndReadAtIsNull(Long userId);
    
    /**
     * Mark all as read for user
     */
    @Modifying
    @Query("UPDATE Notification n SET n.readAt = FUNCTION('NOW') WHERE " +
           "n.user.userId = :userId AND n.readAt IS NULL")
    void markAllAsReadByUserId(Long userId);
    
    /**
     * Delete notifications by user
     */
    void deleteByUser_UserId(Long userId);
    
    /**
     * Delete notifications by entity
     */
    void deleteByEntityTypeAndEntityId(String entityType, Long entityId);
}
