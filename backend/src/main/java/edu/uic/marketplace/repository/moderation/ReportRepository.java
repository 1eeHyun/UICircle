package edu.uic.marketplace.repository.moderation;

import edu.uic.marketplace.model.moderation.Report;
import edu.uic.marketplace.model.moderation.ReportStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReportRepository extends JpaRepository<Report, Long> {
    
    /**
     * Find reports by status
     */
    Page<Report> findByStatus(ReportStatus status, Pageable pageable);
    
    /**
     * Find reports by reporter
     */
    Page<Report> findByReporter_UserId(Long reporterId, Pageable pageable);
    
    /**
     * Find reports by target
     */
    Page<Report> findByTargetTypeAndTargetId(String targetType, Long targetId, Pageable pageable);
    
    /**
     * Find reports by target and status
     */
    List<Report> findByTargetTypeAndTargetIdAndStatus(
            String targetType, Long targetId, ReportStatus status);
    
    /**
     * Check if user already reported target
     */
    boolean existsByReporter_UserIdAndTargetTypeAndTargetId(
            Long reporterId, String targetType, Long targetId);
    
    /**
     * Count pending reports
     */
    Long countByStatus(ReportStatus status);
    
    /**
     * Count reports for target
     */
    Long countByTargetTypeAndTargetId(String targetType, Long targetId);
    
    /**
     * Delete reports by target
     */
    void deleteByTargetTypeAndTargetId(String targetType, Long targetId);
}
