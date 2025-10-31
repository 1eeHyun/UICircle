package edu.uic.marketplace.service.moderation;

import edu.uic.marketplace.dto.request.moderation.CreateReportRequest;
import edu.uic.marketplace.dto.response.moderation.ReportResponse;
import edu.uic.marketplace.model.moderation.Report;
import edu.uic.marketplace.model.moderation.ReportStatus;
import edu.uic.marketplace.model.user.User;
import edu.uic.marketplace.repository.moderation.ReportRepository;
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
@DisplayName("ReportService Unit Test")
class ReportServiceTest {

    @Mock
    private ReportRepository reportRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private ReportServiceImpl reportService;

    private User reporter;
    private Report report;

    @BeforeEach
    void setUp() {

        reporter = User.builder().userId(1L).build();
        report = Report.builder()
                .reportId(1L)
                .reporter(reporter)
                .targetType("listing")
                .targetId(1L)
                .reason("Inappropriate content")
                .status(ReportStatus.PENDING)
                .createdAt(Instant.now())
                .build();
    }

    @Test
    @DisplayName("Create a report - Success")
    void createReport_Success() {

        // Given
        CreateReportRequest request = CreateReportRequest.builder()
                .targetType("listing")
                .targetId(1L)
                .reason("Inappropriate content")
                .build();

        when(userService.findById(1L)).thenReturn(Optional.of(reporter));
        when(reportRepository.save(any(Report.class))).thenReturn(report);

        // When
        ReportResponse response = reportService.createReport(1L, request);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getReason()).isEqualTo("Inappropriate content");
        verify(reportRepository, times(1)).save(any(Report.class));
    }

    @Test
    @DisplayName("Check duplicated report")
    void hasReported() {

        // Given
        when(reportRepository.existsByReporter_UserIdAndTargetTypeAndTargetId(1L, "listing", 1L))
                .thenReturn(true);

        // When
        boolean result = reportService.hasReported(1L, "listing", 1L);

        // Then
        assertThat(result).isTrue();
        verify(reportRepository, times(1))
                .existsByReporter_UserIdAndTargetTypeAndTargetId(1L, "listing", 1L);
    }
}
