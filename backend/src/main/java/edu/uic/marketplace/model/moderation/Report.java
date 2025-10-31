package edu.uic.marketplace.model.moderation;

import edu.uic.marketplace.model.user.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

@Entity
@Table(
        name = "reports",
        indexes = {
                @Index(name = "idx_reports_reporter_id", columnList = "reporter_id"),
                @Index(name = "idx_reports_target_type", columnList = "target_type"),
                @Index(name = "idx_reports_target_id", columnList = "target_id"),
                @Index(name = "idx_reports_status", columnList = "status"),
                @Index(name = "idx_reports_created_at", columnList = "created_at")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Report {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "report_id")
    private Long reportId;

    /**
     * User who filed the report
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reporter_id", nullable = false, foreignKey = @ForeignKey(name = "fk_reports_reporter"))
    private User reporter;

    /**
     * Type of entity being reported (listing, user, message, etc.)
     */
    @Column(name = "target_type", nullable = false, length = 50)
    private String targetType;

    /**
     * ID of the entity being reported
     */
    @Column(name = "target_id", nullable = false)
    private Long targetId;

    /**
     * Report reason category
     */
    @Column(name = "reason", nullable = false, length = 100)
    private String reason;

    /**
     * Detailed description of the report
     */
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    /**
     * Report status
     */
    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private ReportStatus status = ReportStatus.PENDING;

    /**
     * Admin who resolved the report
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "resolved_by", foreignKey = @ForeignKey(name = "fk_reports_resolved_by"))
    private User resolvedBy;

    /**
     * Resolution note
     */
    @Column(name = "resolution_note", columnDefinition = "TEXT")
    private String resolutionNote;

    /**
     * Resolution timestamp
     */
    @Column(name = "resolved_at")
    private Instant resolvedAt;

    /**
     * Creation timestamp
     */
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    /**
     * Helper Methods
     */
    public void resolve(User admin, String note) {
        this.status = ReportStatus.RESOLVED;
        this.resolvedBy = admin;
        this.resolutionNote = note;
        this.resolvedAt = Instant.now();
    }

    public void dismiss(User admin, String note) {
        this.status = ReportStatus.DISMISSED;
        this.resolvedBy = admin;
        this.resolutionNote = note;
        this.resolvedAt = Instant.now();
    }

    public boolean isPending() {
        return status == ReportStatus.PENDING;
    }

    public boolean isResolved() {
        return status == ReportStatus.RESOLVED || status == ReportStatus.DISMISSED;
    }
}
