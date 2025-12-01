package edu.uic.marketplace.repository.moderation;

import edu.uic.marketplace.model.moderation.Block;
import edu.uic.marketplace.model.moderation.Block.BlockId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import jakarta.persistence.QueryHint;
import java.util.List;
import java.util.Optional;

@Repository
public interface BlockRepository extends JpaRepository<Block, BlockId> {

    /**
     * Find block relationship by blocker ID and blocked ID
     */
    @QueryHints(@QueryHint(name = "org.hibernate.readOnly", value = "true"))
    @Query("SELECT b FROM Block b WHERE b.id.blockerId = :blockerId AND b.id.blockedId = :blockedId")
    Optional<Block> findByBlockerIdAndBlockedId(@Param("blockerId") Long blockerId,
                                                @Param("blockedId") Long blockedId);

    /**
     * Find block relationship by blocker username and blocked username
     */
    @QueryHints(@QueryHint(name = "org.hibernate.readOnly", value = "true"))
    @Query("SELECT b FROM Block b " +
            "JOIN b.blocker blocker " +
            "JOIN b.blocked blocked " +
            "WHERE blocker.username = :blockerUsername AND blocked.username = :blockedUsername")
    Optional<Block> findByBlockerUsernameAndBlockedUsername(@Param("blockerUsername") String blockerUsername,
                                                            @Param("blockedUsername") String blockedUsername);

    /**
     * Find all users blocked by a user (with user information)
     * Optimized with fetch join to prevent N+1 queries
     */
    @Query("SELECT b FROM Block b " +
            "JOIN FETCH b.blocker " +
            "JOIN FETCH b.blocked " +
            "WHERE b.id.blockerId = :blockerId " +
            "ORDER BY b.blockedAt DESC")
    @QueryHints(@QueryHint(name = "org.hibernate.readOnly", value = "true"))
    List<Block> findByBlockerIdWithUsers(@Param("blockerId") Long blockerId);

    /**
     * Find all users blocked by a user by username (with user information)
     */
    @Query("SELECT b FROM Block b " +
            "JOIN FETCH b.blocker blocker " +
            "JOIN FETCH b.blocked " +
            "WHERE blocker.username = :blockerUsername " +
            "ORDER BY b.blockedAt DESC")
    @QueryHints(@QueryHint(name = "org.hibernate.readOnly", value = "true"))
    List<Block> findByBlockerUsernameWithUsers(@Param("blockerUsername") String blockerUsername);

    /**
     * Find all users who blocked this user (with user information)
     * Optimized with fetch join to prevent N+1 queries
     */
    @Query("SELECT b FROM Block b " +
            "JOIN FETCH b.blocker " +
            "JOIN FETCH b.blocked " +
            "WHERE b.id.blockedId = :blockedId " +
            "ORDER BY b.blockedAt DESC")
    @QueryHints(@QueryHint(name = "org.hibernate.readOnly", value = "true"))
    List<Block> findByBlockedIdWithUsers(@Param("blockedId") Long blockedId);

    /**
     * Find all users who blocked this user by username (with user information)
     */
    @Query("SELECT b FROM Block b " +
            "JOIN FETCH b.blocker " +
            "JOIN FETCH b.blocked blocked " +
            "WHERE blocked.username = :blockedUsername " +
            "ORDER BY b.blockedAt DESC")
    @QueryHints(@QueryHint(name = "org.hibernate.readOnly", value = "true"))
    List<Block> findByBlockedUsernameWithUsers(@Param("blockedUsername") String blockedUsername);

    /**
     * Check if user is blocked by blocker ID and blocked ID
     * Optimized with EXISTS-style query
     */
    @Query("SELECT CASE WHEN COUNT(b) > 0 THEN true ELSE false END " +
            "FROM Block b WHERE b.id.blockerId = :blockerId AND b.id.blockedId = :blockedId")
    boolean existsByBlockerIdAndBlockedId(@Param("blockerId") Long blockerId,
                                          @Param("blockedId") Long blockedId);


    /**
     * Get list of usernames who blocked this user
     */
    @Query("SELECT b.blocker.username FROM Block b WHERE b.blocked.username = :blockedUsername")
    List<String> findBlockerUsernamesByBlockedUsername(@Param("blockedUsername") String blockedUsername);

    /**
     * Check if user is blocked by username
     */
    @Query("SELECT CASE WHEN COUNT(b) > 0 THEN true ELSE false END " +
            "FROM Block b " +
            "WHERE b.blocker.username = :blockerUsername AND b.blocked.username = :blockedUsername")
    boolean existsByBlockerUsernameAndBlockedUsername(@Param("blockerUsername") String blockerUsername,
                                                      @Param("blockedUsername") String blockedUsername);

    /**
     * Check if there's any block relationship between two users (bidirectional check)
     */
    @Query("SELECT CASE WHEN COUNT(b) > 0 THEN true ELSE false END FROM Block b WHERE " +
            "(b.id.blockerId = :userId1 AND b.id.blockedId = :userId2) OR " +
            "(b.id.blockerId = :userId2 AND b.id.blockedId = :userId1)")
    boolean hasBlockRelationshipByIds(@Param("userId1") Long userId1,
                                      @Param("userId2") Long userId2);

    /**
     * Check if there's any block relationship between two users by username (bidirectional check)
     */
    @Query("SELECT CASE WHEN COUNT(b) > 0 THEN true ELSE false END FROM Block b " +
            "WHERE (b.blocker.username = :username1 AND b.blocked.username = :username2) OR " +
            "(b.blocker.username = :username2 AND b.blocked.username = :username1)")
    boolean hasBlockRelationshipByUsernames(@Param("username1") String username1,
                                            @Param("username2") String username2);

    /**
     * Get list of blocks by userId
     */
    @Query("SELECT b FROM Block b WHERE b.blocker.userId = :blockerId")
    List<Block> findByBlockerId(@Param("blockerId") Long blockerId);

    /**
     * Get list of blocked usernames
     */
    @Query("SELECT b.blocked.username FROM Block b WHERE b.blocker.username = :blockerUsername")
    List<String> findBlockedUsernamesByBlockerUsername(@Param("blockerUsername") String blockerUsername);

    /**
     * Count total blocks by blocker ID
     */
    @Query("SELECT COUNT(b) FROM Block b WHERE b.id.blockerId = :blockerId")
    long countByBlockerId(@Param("blockerId") Long blockerId);

    /**
     * Count total blocks by blocker username
     */
    @Query("SELECT COUNT(b) FROM Block b WHERE b.blocker.username = :blockerUsername")
    long countByBlockerUsername(@Param("blockerUsername") String blockerUsername);

    /**
     * Count how many users blocked this user
     */
    @Query("SELECT COUNT(b) FROM Block b WHERE b.id.blockedId = :blockedId")
    long countByBlockedId(@Param("blockedId") Long blockedId);

    /**
     * Delete block relationship by blocker ID and blocked ID
     */
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("DELETE FROM Block b WHERE b.id.blockerId = :blockerId AND b.id.blockedId = :blockedId")
    void deleteByBlockerIdAndBlockedId(@Param("blockerId") Long blockerId,
                                       @Param("blockedId") Long blockedId);

    /**
     * Delete block relationship by blocker username and blocked username
     */
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("DELETE FROM Block b WHERE b.blocker.username = :blockerUsername AND b.blocked.username = :blockedUsername")
    void deleteByBlockerUsernameAndBlockedUsername(@Param("blockerUsername") String blockerUsername,
                                                   @Param("blockedUsername") String blockedUsername);

    /**
     * Delete all blocks created by a user (by blocker ID)
     */
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("DELETE FROM Block b WHERE b.id.blockerId = :blockerId")
    void deleteByBlockerId(@Param("blockerId") Long blockerId);

    /**
     * Delete all blocks created by a user (by blocker username)
     */
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("DELETE FROM Block b WHERE b.blocker.username = :blockerUsername")
    void deleteByBlockerUsername(@Param("blockerUsername") String blockerUsername);

    /**
     * Delete all blocks where user is blocked (by blocked ID)
     */
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("DELETE FROM Block b WHERE b.id.blockedId = :blockedId")
    void deleteByBlockedId(@Param("blockedId") Long blockedId);

    /**
     * Find block by public ID
     */
    @QueryHints(@QueryHint(name = "org.hibernate.readOnly", value = "true"))
    @Query("SELECT b FROM Block b " +
            "JOIN FETCH b.blocker " +
            "JOIN FETCH b.blocked " +
            "WHERE b.publicId = :publicId")
    Optional<Block> findByPublicIdWithUsers(@Param("publicId") String publicId);
}