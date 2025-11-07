package edu.uic.marketplace.model.search;

import edu.uic.marketplace.model.user.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(
        name = "saved_searches",
        indexes = {
                @Index(name = "idx_saved_searches_user_id", columnList = "user_id"),
                @Index(name = "idx_saved_searches_created_at", columnList = "created_at")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SavedSearch {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "saved_search_id")
    private Long savedSearchId;

    @Column(name = "public_id", nullable = false, updatable = false, unique = true, length = 36)
    private String publicId;

    /**
     * User who saved the search
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, foreignKey = @ForeignKey(name = "fk_saved_searches_user"))
    private User user;

    /**
     * Name/label for the saved search
     */
    @Column(name = "name", length = 100)
    private String name;

    /**
     * Search query text
     */
    @Column(name = "query", length = 500)
    private String query;

    /**
     * Search filters as JSON string
     */
    @Column(name = "filters", columnDefinition = "TEXT")
    private String filters;

    /**
     * Hash of query + filters for duplicate detection
     */
    @Column(name = "query_hash", length = 64)
    private String queryHash;

    /**
     * Creation timestamp
     */
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @PrePersist
    public void prePersist() {
        if (this.publicId == null)
            this.publicId = UUID.randomUUID().toString();
    }
}
