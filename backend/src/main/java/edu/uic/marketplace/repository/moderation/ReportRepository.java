package edu.uic.marketplace.repository.moderation;

import edu.uic.marketplace.model.moderation.Report;
import edu.uic.marketplace.model.moderation.ReportStatus;
import edu.uic.marketplace.model.moderation.ReportTargetType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReportRepository extends JpaRepository<Report, Long> {

    Optional<Report> findByPublicId(String publicId);

    @Query("""
           SELECT r FROM Report r
           LEFT JOIN FETCH r.reporter
           LEFT JOIN FETCH r.resolvedBy
           WHERE r.status = :status
           ORDER BY r.createdAt DESC
           """)
    Page<Report> findByStatusWithDetails(@Param("status") ReportStatus status, Pageable pageable);

    Page<Report> findByStatus(ReportStatus status, Pageable pageable);

    @Query("""
           SELECT r FROM Report r
           LEFT JOIN FETCH r.reporter
           LEFT JOIN FETCH r.resolvedBy
           WHERE r.reporter.username = :username
           ORDER BY r.createdAt DESC
           """)
    Page<Report> findByReporter_UsernameWithDetails(@Param("username") String username, Pageable pageable);

    Page<Report> findByReporter_Username(String username, Pageable pageable);

    @Query("""
           SELECT r FROM Report r
           LEFT JOIN FETCH r.reporter
           LEFT JOIN FETCH r.resolvedBy
           WHERE r.targetType = :targetType 
             AND r.targetPublicId = :targetPublicId
           ORDER BY r.createdAt DESC
           """)
    Page<Report> findByTargetTypeAndTargetPublicIdWithDetails(
            @Param("targetType") ReportTargetType targetType,
            @Param("targetPublicId") String targetPublicId,
            Pageable pageable
    );

    Page<Report> findByTargetTypeAndTargetPublicId(
            ReportTargetType targetType,
            String targetPublicId,
            Pageable pageable
    );

    List<Report> findByTargetTypeAndTargetPublicIdAndStatus(
            ReportTargetType targetType,
            String targetPublicId,
            ReportStatus status
    );

    boolean existsByReporter_UsernameAndTargetTypeAndTargetPublicId(
            String username,
            ReportTargetType targetType,
            String targetPublicId
    );

    Long countByStatus(ReportStatus status);

    Long countByTargetTypeAndTargetPublicId(
            ReportTargetType targetType,
            String targetPublicId
    );

    void deleteByTargetTypeAndTargetPublicId(
            ReportTargetType targetType,
            String targetPublicId
    );

    @Query("""
           SELECT r FROM Report r
           LEFT JOIN FETCH r.reporter
           LEFT JOIN FETCH r.resolvedBy
           ORDER BY r.createdAt DESC
           """)
    Page<Report> findAllWithDetails(Pageable pageable);
}
