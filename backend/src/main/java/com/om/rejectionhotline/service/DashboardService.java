package com.om.rejectionhotline.service;

import com.om.rejectionhotline.dto.DashboardSummaryResponse;
import com.om.rejectionhotline.entity.enums.ApplicationStatus;
import com.om.rejectionhotline.entity.enums.EmailStatus;
import com.om.rejectionhotline.repository.JobApplicationRepository;
import com.om.rejectionhotline.util.FollowUpRules;
import com.om.rejectionhotline.util.SecurityUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class DashboardService {

    private final JobApplicationRepository jobApplicationRepository;

    public DashboardService(JobApplicationRepository jobApplicationRepository) {
        this.jobApplicationRepository = jobApplicationRepository;
    }

    public DashboardSummaryResponse getSummary() {
        Long userId = SecurityUtils.getCurrentUserId();
        LocalDate today = LocalDate.now();
        LocalDate threshold = FollowUpRules.getFollowUpThresholdDate(today);

        long total = jobApplicationRepository.countByUserId(userId);
        long appliedToday = jobApplicationRepository.countByUserIdAndAppliedDate(userId, today);
        long needFollowUp = jobApplicationRepository.countNeedFollowUp(userId, EmailStatus.SENT, threshold);
        long interviews = jobApplicationRepository.countByUserIdAndApplicationStatus(userId, ApplicationStatus.INTERVIEW);
        long rejected = jobApplicationRepository.countByUserIdAndApplicationStatus(userId, ApplicationStatus.REJECTED);
        long offers = jobApplicationRepository.countByUserIdAndApplicationStatus(userId, ApplicationStatus.OFFER);

        long emailSent = jobApplicationRepository.countEmailSent(userId, EmailStatus.SENT);
        long replied = jobApplicationRepository.countReplied(userId, EmailStatus.SENT);
        long followUpSent = jobApplicationRepository.countFollowUpSent(userId);
        long followUpWithReply = jobApplicationRepository.countFollowUpSentWithReply(userId);

        double responseRate = emailSent == 0 ? 0.0 : (double) replied / emailSent * 100.0;
        double followUpSuccessRate = followUpSent == 0 ? 0.0 : (double) followUpWithReply / followUpSent * 100.0;

        return DashboardSummaryResponse.builder()
                .totalApplications(total)
                .appliedToday(appliedToday)
                .needFollowUp(needFollowUp)
                .interviews(interviews)
                .rejected(rejected)
                .offers(offers)
                .responseRate(round(responseRate))
                .followUpSuccessRate(round(followUpSuccessRate))
                .build();
    }

    private double round(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}
