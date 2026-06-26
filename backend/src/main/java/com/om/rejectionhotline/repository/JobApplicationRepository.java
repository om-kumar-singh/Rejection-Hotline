package com.om.rejectionhotline.repository;

import com.om.rejectionhotline.entity.JobApplication;
import com.om.rejectionhotline.entity.enums.ApplicationStatus;
import com.om.rejectionhotline.entity.enums.EmailStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface JobApplicationRepository extends JpaRepository<JobApplication, Long>, JpaSpecificationExecutor<JobApplication> {

    Optional<JobApplication> findByIdAndUserId(Long id, Long userId);

    Page<JobApplication> findByUserId(Long userId, Pageable pageable);

    long countByUserId(Long userId);

    long countByUserIdAndAppliedDate(Long userId, LocalDate appliedDate);

    long countByUserIdAndApplicationStatus(Long userId, ApplicationStatus status);

    @Query("""
            SELECT COUNT(j) FROM JobApplication j
            WHERE j.user.id = :userId
            AND j.emailStatus = :emailStatus
            AND j.replyReceived = false
            AND j.followUpSentCount = 0
            AND j.followUpReminderEnabled = true
            AND j.appliedDate <= :followUpThreshold
            """)
    long countNeedFollowUp(@Param("userId") Long userId,
                           @Param("emailStatus") EmailStatus emailStatus,
                           @Param("followUpThreshold") LocalDate followUpThreshold);

    @Query("""
            SELECT j FROM JobApplication j
            WHERE j.emailStatus = :emailStatus
            AND j.replyReceived = false
            AND j.followUpSentCount = 0
            AND j.followUpReminderEnabled = true
            AND j.appliedDate <= :followUpThreshold
            """)
    List<JobApplication> findAllEligibleForFollowUp(@Param("emailStatus") EmailStatus emailStatus,
                                                       @Param("followUpThreshold") LocalDate followUpThreshold);

    @Query("""
            SELECT COUNT(j) FROM JobApplication j
            WHERE j.user.id = :userId
            AND j.emailStatus = :emailStatus
            AND j.replyReceived = true
            """)
    long countReplied(@Param("userId") Long userId, @Param("emailStatus") EmailStatus emailStatus);

    @Query("""
            SELECT COUNT(j) FROM JobApplication j
            WHERE j.user.id = :userId
            AND j.emailStatus = :emailStatus
            """)
    long countEmailSent(@Param("userId") Long userId, @Param("emailStatus") EmailStatus emailStatus);

    @Query("""
            SELECT COUNT(j) FROM JobApplication j
            WHERE j.user.id = :userId
            AND j.followUpSentCount >= 1
            """)
    long countFollowUpSent(@Param("userId") Long userId);

    @Query("""
            SELECT COUNT(j) FROM JobApplication j
            WHERE j.user.id = :userId
            AND j.followUpSentCount >= 1
            AND j.replyReceived = true
            """)
    long countFollowUpSentWithReply(@Param("userId") Long userId);

    boolean existsByUserIdAndCompanyNameIgnoreCaseAndJobRoleIgnoreCaseAndAppliedDate(
            Long userId, String companyName, String jobRole, LocalDate appliedDate);

    @Query("""
            SELECT j.companyName, COUNT(j) FROM JobApplication j
            WHERE j.user.id = :userId
            GROUP BY j.companyName
            ORDER BY COUNT(j) DESC
            """)
    List<Object[]> countByCompany(@Param("userId") Long userId, Pageable pageable);

    @Query(value = """
            SELECT TO_CHAR(applied_date, 'YYYY-MM') AS month, COUNT(*) AS total
            FROM job_applications
            WHERE user_id = :userId
            AND applied_date >= :fromDate
            AND applied_date <= :toDate
            GROUP BY TO_CHAR(applied_date, 'YYYY-MM')
            ORDER BY month
            """, nativeQuery = true)
    List<Object[]> countByMonth(@Param("userId") Long userId,
                                @Param("fromDate") LocalDate fromDate,
                                @Param("toDate") LocalDate toDate);
}
