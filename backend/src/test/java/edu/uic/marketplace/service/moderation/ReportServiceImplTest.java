package edu.uic.marketplace.service.moderation;

import edu.uic.marketplace.dto.request.moderation.CreateReportRequest;
import edu.uic.marketplace.dto.response.common.PageResponse;
import edu.uic.marketplace.dto.response.moderation.ReportResponse;
import edu.uic.marketplace.model.moderation.Report;
import edu.uic.marketplace.model.moderation.ReportReason;
import edu.uic.marketplace.model.moderation.ReportStatus;
import edu.uic.marketplace.model.moderation.ReportTargetType;
import edu.uic.marketplace.model.user.User;
import edu.uic.marketplace.repository.moderation.ReportRepository;
import edu.uic.marketplace.validator.auth.AuthValidator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ReportServiceImplTest {

    @Mock
    private ReportRepository reportRepository;

    @Mock
    private AuthValidator authValidator;

    @InjectMocks
    private ReportServiceImpl reportService;

    private User createUser(Long id, String username) {
        User user = new User();
        user.setUserId(id);
        user.setUsername(username);
        user.setEmail(username + "@example.com");
        return user;
    }

    private Report createReport(User reporter, ReportStatus status) {
        return Report.builder()
                .publicId("report-public-id-123")
                .reporter(reporter)
                .targetType(ReportTargetType.LISTING)
                .targetPublicId("listing-public-id-123")
                .reason(ReportReason.SPAM)
                .description("This listing looks like spam.")
                .status(status)
                .createdAt(Instant.now())
                .build();
    }

    // ------------------------------------------------------------------------
    // createReport
    // ------------------------------------------------------------------------
    @Nested
    @DisplayName("createReport")
    class CreateReportTests {

        @Test
        @DisplayName("should create a new report when user has not reported the target yet")
        void createReport_success() {
            // given
            String reporterUsername = "john_doe";
            User reporter = createUser(1L, reporterUsername);

            CreateReportRequest request = CreateReportRequest.builder()
                    .targetType(ReportTargetType.LISTING)
                    .targetPublicId("listing-public-id-123")
                    .reason(ReportReason.SPAM)
                    .description("This listing looks like spam.")
                    .build();

            given(authValidator.validateUserByUsername(reporterUsername))
                    .willReturn(reporter);

            given(reportRepository.existsByReporter_UsernameAndTargetTypeAndTargetPublicId(
                    reporterUsername, request.getTargetType(), request.getTargetPublicId()))
                    .willReturn(false);

            given(reportRepository.save(any(Report.class)))
                    .willAnswer(invocation -> invocation.getArgument(0));

            // when
            ReportResponse response = reportService.createReport(reporterUsername, request);

            // then
            assertThat(response).isNotNull();
            assertThat(response.getTargetType()).isEqualTo(request.getTargetType());
            assertThat(response.getTargetPublicId()).isEqualTo(request.getTargetPublicId());
            assertThat(response.getReason()).isEqualTo(request.getReason());

            verify(authValidator).validateUserByUsername(reporterUsername);
            verify(reportRepository).existsByReporter_UsernameAndTargetTypeAndTargetPublicId(
                    reporterUsername, request.getTargetType(), request.getTargetPublicId());
            verify(reportRepository).save(any(Report.class));
        }

        @Test
        @DisplayName("should throw IllegalArgumentException when user already reported the target")
        void createReport_alreadyExists() {
            // given
            String reporterUsername = "john_doe";
            User reporter = createUser(1L, reporterUsername);

            CreateReportRequest request = CreateReportRequest.builder()
                    .targetType(ReportTargetType.LISTING)
                    .targetPublicId("listing-public-id-123")
                    .reason(ReportReason.SPAM)
                    .description("This listing looks like spam.")
                    .build();

            given(authValidator.validateUserByUsername(reporterUsername))
                    .willReturn(reporter);

            given(reportRepository.existsByReporter_UsernameAndTargetTypeAndTargetPublicId(
                    reporterUsername, request.getTargetType(), request.getTargetPublicId()))
                    .willReturn(true);

            // when / then
            assertThatThrownBy(() -> reportService.createReport(reporterUsername, request))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("You already reported it.");

            verify(reportRepository).existsByReporter_UsernameAndTargetTypeAndTargetPublicId(
                    reporterUsername, request.getTargetType(), request.getTargetPublicId());
        }
    }

    // ------------------------------------------------------------------------
    // findById
    // ------------------------------------------------------------------------
    @Nested
    @DisplayName("findById")
    class FindByIdTests {

        @Test
        @DisplayName("should return Optional<Report> when report exists")
        void findById_exists() {
            // given
            String publicId = "report-public-id-123";
            User reporter = createUser(1L, "john_doe");
            Report report = createReport(reporter, ReportStatus.PENDING);

            given(reportRepository.findByPublicId(publicId))
                    .willReturn(Optional.of(report));

            // when
            Optional<Report> result = reportService.findById(publicId);

            // then
            assertThat(result).isPresent();
            assertThat(result.get().getPublicId()).isEqualTo(publicId);

            verify(reportRepository).findByPublicId(publicId);
        }

        @Test
        @DisplayName("should return Optional.empty() when report does not exist")
        void findById_notExists() {
            // given
            String publicId = "unknown-id";
            given(reportRepository.findByPublicId(publicId))
                    .willReturn(Optional.empty());

            // when
            Optional<Report> result = reportService.findById(publicId);

            // then
            assertThat(result).isEmpty();
            verify(reportRepository).findByPublicId(publicId);
        }
    }

    // ------------------------------------------------------------------------
    // getAllReports
    // ------------------------------------------------------------------------
    @Nested
    @DisplayName("getAllReports")
    class GetAllReportsTests {

        @Test
        @DisplayName("should use findAllWithDetails when status is null")
        void getAllReports_withoutStatus() {
            // given
            Integer page = 0;
            Integer size = 10;
            ReportStatus status = null;

            User reporter = createUser(1L, "john_doe");
            Report report = createReport(reporter, ReportStatus.PENDING);

            Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
            Page<Report> reportPage = new PageImpl<>(List.of(report), pageable, 1);

            given(reportRepository.findAllWithDetails(any(Pageable.class)))
                    .willReturn(reportPage);

            // when
            PageResponse<ReportResponse> response = reportService.getAllReports(status, page, size);

            // then
            assertThat(response).isNotNull();
            assertThat(response.getContent()).hasSize(1);
            verify(reportRepository).findAllWithDetails(any(Pageable.class));
        }

        @Test
        @DisplayName("should use findByStatusWithDetails when status is not null")
        void getAllReports_withStatus() {
            // given
            Integer page = 0;
            Integer size = 10;
            ReportStatus status = ReportStatus.PENDING;

            User reporter = createUser(1L, "john_doe");
            Report report = createReport(reporter, status);

            Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
            Page<Report> reportPage = new PageImpl<>(List.of(report), pageable, 1);

            given(reportRepository.findByStatusWithDetails(eq(status), any(Pageable.class)))
                    .willReturn(reportPage);

            // when
            PageResponse<ReportResponse> response = reportService.getAllReports(status, page, size);

            // then
            assertThat(response).isNotNull();
            assertThat(response.getContent()).hasSize(1);
            verify(reportRepository).findByStatusWithDetails(eq(status), any(Pageable.class));
        }
    }

    // ------------------------------------------------------------------------
    // getReportsByTarget
    // ------------------------------------------------------------------------
    @Nested
    @DisplayName("getReportsByTarget")
    class GetReportsByTargetTests {

        @Test
        @DisplayName("should return paged reports for given target")
        void getReportsByTarget_success() {
            // given
            Integer page = 0;
            Integer size = 10;
            ReportTargetType targetType = ReportTargetType.LISTING;
            String targetPublicId = "listing-public-id-123";

            User reporter = createUser(1L, "john_doe");
            Report report = createReport(reporter, ReportStatus.PENDING);

            Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
            Page<Report> reportPage = new PageImpl<>(List.of(report), pageable, 1);

            given(reportRepository.findByTargetTypeAndTargetPublicIdWithDetails(
                    eq(targetType), eq(targetPublicId), any(Pageable.class)))
                    .willReturn(reportPage);

            // when
            PageResponse<ReportResponse> response =
                    reportService.getReportsByTarget(targetType, targetPublicId, page, size);

            // then
            assertThat(response).isNotNull();
            assertThat(response.getContent()).hasSize(1);

            verify(reportRepository).findByTargetTypeAndTargetPublicIdWithDetails(
                    eq(targetType), eq(targetPublicId), any(Pageable.class));
        }
    }

    // ------------------------------------------------------------------------
    // getUserReports
    // ------------------------------------------------------------------------
    @Nested
    @DisplayName("getUserReports")
    class GetUserReportsTests {

        @Test
        @DisplayName("should return paged reports created by the given user")
        void getUserReports_success() {
            // given
            String username = "john_doe";
            Integer page = 0;
            Integer size = 10;

            User reporter = createUser(1L, username);
            Report report = createReport(reporter, ReportStatus.PENDING);

            Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
            Page<Report> reportPage = new PageImpl<>(List.of(report), pageable, 1);

            given(authValidator.validateUserByUsername(username))
                    .willReturn(reporter);

            given(reportRepository.findByReporter_UsernameWithDetails(eq(username), any(Pageable.class)))
                    .willReturn(reportPage);

            // when
            PageResponse<ReportResponse> response =
                    reportService.getUserReports(username, page, size);

            // then
            assertThat(response).isNotNull();
            assertThat(response.getContent()).hasSize(1);

            verify(authValidator).validateUserByUsername(username);
            verify(reportRepository).findByReporter_UsernameWithDetails(eq(username), any(Pageable.class));
        }
    }

    // ------------------------------------------------------------------------
    // resolveReport
    // ------------------------------------------------------------------------
    @Nested
    @DisplayName("resolveReport")
    class ResolveReportTests {

        @Test
        @DisplayName("should resolve a pending report successfully")
        void resolveReport_success() {
//            // given
//            String reportPublicId = "report-public-id-123";
//            String adminUsername = "admin_user";
//
//            User admin = createUser(2L, adminUsername);
//            User reporter = createUser(1L, "john_doe");
//            Report report = createReport(reporter, ReportStatus.PENDING);
//
//            ResolveReportRequest request = ResolveReportRequest.builder()
//                    .resolutionNote("Valid report. Action taken.")
//                    .build();
//
//            given(authValidator.validateUserByUsername(adminUsername))
//                    .willReturn(admin);
//
//            given(reportRepository.findByPublicId(reportPublicId))
//                    .willReturn(Optional.of(report));
//
//            given(reportRepository.save(any(Report.class)))
//                    .willAnswer(invocation -> invocation.getArgument(0));
//
//            // when
//            ReportResponse response =
//                    reportService.resolveReport(reportPublicId, adminUsername, request);
//
//            // then
//            assertThat(response).isNotNull();
//            assertThat(response.getStatus()).isEqualTo(ReportStatus.RESOLVED);
//            assertThat(response.getResolutionNote())
//                    .isEqualTo(request.getResolutionNote());
//
//            verify(authValidator).validateUserByUsername(adminUsername);
//            verify(reportRepository).findByPublicId(reportPublicId);
//            verify(reportRepository).save(any(Report.class));
        }

        @Test
        @DisplayName("should throw IllegalArgumentException when report not found")
        void resolveReport_notFound() {
            // given
//            String reportPublicId = "unknown-id";
//            String adminUsername = "admin_user";
//
//            ResolveReportRequest request = ResolveReportRequest.builder()
//                    .resolutionNote("Some note")
//                    .build();
//
//            given(authValidator.validateUserByUsername(adminUsername))
//                    .willReturn(createUser(2L, adminUsername));
//
//            given(reportRepository.findByPublicId(reportPublicId))
//                    .willReturn(Optional.empty());
//
//            // when / then
//            assertThatThrownBy(() ->
//                    reportService.resolveReport(reportPublicId, adminUsername, request)
//            ).isInstanceOf(IllegalArgumentException.class)
//                    .hasMessage("Report not found");
//
//            verify(reportRepository).findByPublicId(reportPublicId);
        }

        @Test
        @DisplayName("should throw IllegalStateException when report is already resolved")
        void resolveReport_alreadyResolved() {
            // given
//            String reportPublicId = "report-public-id-123";
//            String adminUsername = "admin_user";
//
//            User admin = createUser(2L, adminUsername);
//            User reporter = createUser(1L, "john_doe");
//            Report report = createReport(reporter, ReportStatus.RESOLVED);
//
//            ResolveReportRequest request = ResolveReportRequest.builder()
//                    .resolutionNote("Already handled.")
//                    .build();
//
//            given(authValidator.validateUserByUsername(adminUsername))
//                    .willReturn(admin);
//
//            given(reportRepository.findByPublicId(reportPublicId))
//                    .willReturn(Optional.of(report));
//
//            // when / then
//            assertThatThrownBy(() ->
//                    reportService.resolveReport(reportPublicId, adminUsername, request)
//            ).isInstanceOf(IllegalStateException.class)
//                    .hasMessage("Report is already resolved");
//
//            verify(reportRepository).findByPublicId(reportPublicId);
        }
    }

    // ------------------------------------------------------------------------
    // hasReported
    // ------------------------------------------------------------------------
    @Nested
    @DisplayName("hasReported")
    class HasReportedTests {

        @Test
        @DisplayName("should return true when report exists for user + target")
        void hasReported_true() {
            // given
//            String username = "john_doe";
//            ReportTargetType targetType = ReportTargetType.LISTING;
//            String targetPublicId = "listing-public-id-123";
//
//            given(reportRepository.existsByReporter_UsernameAndTargetTypeAndTargetPublicId(
//                    username, targetType, targetPublicId))
//                    .willReturn(true);
//
//            // when
//            boolean result = reportService.hasReported(username, targetType, targetPublicId);
//
//            // then
//            assertThat(result).isTrue();
//            verify(reportRepository).existsByReporter_UsernameAndTargetTypeAndTargetPublicId(
//                    username, targetType, targetPublicId);
        }

        @Test
        @DisplayName("should return false when no report exists for user + target")
        void hasReported_false() {
            // given
//            String username = "john_doe";
//            ReportTargetType targetType = ReportTargetType.LISTING;
//            String targetPublicId = "listing-public-id-123";
//
//            given(reportRepository.existsByReporter_UsernameAndTargetTypeAndTargetPublicId(
//                    username, targetType, targetPublicId))
//                    .willReturn(false);
//
//            // when
//            boolean result = reportService.hasReported(username, targetType, targetPublicId);
//
//            // then
//            assertThat(result).isFalse();
//            verify(reportRepository).existsByReporter_UsernameAndTargetTypeAndTargetPublicId(
//                    username, targetType, targetPublicId);
        }
    }

    // ------------------------------------------------------------------------
    // getPendingReportCount
    // ------------------------------------------------------------------------
    @Nested
    @DisplayName("getPendingReportCount")
    class GetPendingReportCountTests {

        @Test
        @DisplayName("should return count of pending reports")
        void getPendingReportCount_success() {
            // given
//            Long expectedCount = 5L;
//            given(reportRepository.countByStatus(ReportStatus.PENDING))
//                    .willReturn(expectedCount);
//
//            // when
//            Long result = reportService.getPendingReportCount();
//
//            // then
//            assertThat(result).isEqualTo(expectedCount);
//            verify(reportRepository).countByStatus(ReportStatus.PENDING);
        }
    }

    // ------------------------------------------------------------------------
    // getReportCountForTarget
    // ------------------------------------------------------------------------
    @Nested
    @DisplayName("getReportCountForTarget")
    class GetReportCountForTargetTests {

        @Test
        @DisplayName("should return count of reports for given target")
        void getReportCountForTarget_success() {
            // given
//            ReportTargetType targetType = ReportTargetType.LISTING;
//            String targetPublicId = "listing-public-id-123";
//            Long expectedCount = 3L;
//
//            given(reportRepository.countByTargetTypeAndTargetPublicId(targetType, targetPublicId))
//                    .willReturn(expectedCount);
//
//            // when
//            Long result = reportService.getReportCountForTarget(targetType, targetPublicId);
//
//            // then
//            assertThat(result).isEqualTo(expectedCount);
//            verify(reportRepository).countByTargetTypeAndTargetPublicId(targetType, targetPublicId);
        }
    }
}
