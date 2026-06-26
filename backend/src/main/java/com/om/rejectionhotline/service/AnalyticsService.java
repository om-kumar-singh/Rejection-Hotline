package com.om.rejectionhotline.service;

import com.om.rejectionhotline.dto.AnalyticsRatesResponse;
import com.om.rejectionhotline.dto.CompanyApplicationsResponse;
import com.om.rejectionhotline.dto.MonthlyApplicationsResponse;
import com.om.rejectionhotline.entity.enums.ApplicationStatus;
import com.om.rejectionhotline.entity.enums.EmailStatus;
import com.om.rejectionhotline.repository.JobApplicationRepository;
import com.om.rejectionhotline.util.SecurityUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class AnalyticsService {

    private final JobApplicationRepository jobApplicationRepository;

    public AnalyticsService(JobApplicationRepository jobApplicationRepository) {
        this.jobApplicationRepository = jobApplicationRepository;
    }

    public MonthlyApplicationsResponse getApplicationsByMonth(LocalDate from, LocalDate to) {
        Long userId = SecurityUtils.getCurrentUserId();
        LocalDate fromDate = from != null ? from : LocalDate.now().minusMonths(11).withDayOfMonth(1);
        LocalDate toDate = to != null ? to : LocalDate.now();

        List<MonthlyApplicationsResponse.MonthlyCount> data = jobApplicationRepository
                .countByMonth(userId, fromDate, toDate)
                .stream()
                .map(row -> MonthlyApplicationsResponse.MonthlyCount.builder()
                        .month((String) row[0])
                        .count(((Number) row[1]).longValue())
                        .build())
                .toList();

        return MonthlyApplicationsResponse.builder().data(data).build();
    }

    public CompanyApplicationsResponse getApplicationsByCompany(int limit) {
        Long userId = SecurityUtils.getCurrentUserId();
        List<CompanyApplicationsResponse.CompanyCount> data = jobApplicationRepository
                .countByCompany(userId, PageRequest.of(0, limit))
                .stream()
                .map(row -> CompanyApplicationsResponse.CompanyCount.builder()
                        .companyName((String) row[0])
                        .count(((Number) row[1]).longValue())
                        .build())
                .toList();

        return CompanyApplicationsResponse.builder().data(data).build();
    }

    public AnalyticsRatesResponse getRates(LocalDate from, LocalDate to) {
        Long userId = SecurityUtils.getCurrentUserId();
        long total = jobApplicationRepository.countByUserId(userId);
        if (total == 0) {
            return AnalyticsRatesResponse.builder()
                    .responseRate(0)
                    .followUpRate(0)
                    .interviewRate(0)
                    .offerRate(0)
                    .build();
        }

        long emailSent = jobApplicationRepository.countEmailSent(userId, EmailStatus.SENT);
        long replied = jobApplicationRepository.countReplied(userId, EmailStatus.SENT);
        long followUpSent = jobApplicationRepository.countFollowUpSent(userId);
        long interviews = jobApplicationRepository.countByUserIdAndApplicationStatus(userId, ApplicationStatus.INTERVIEW);
        long offers = jobApplicationRepository.countByUserIdAndApplicationStatus(userId, ApplicationStatus.OFFER);

        return AnalyticsRatesResponse.builder()
                .responseRate(rate(replied, emailSent))
                .followUpRate(rate(followUpSent, total))
                .interviewRate(rate(interviews, total))
                .offerRate(rate(offers, total))
                .build();
    }

    private double rate(long numerator, long denominator) {
        if (denominator == 0) {
            return 0.0;
        }
        return Math.round((double) numerator / denominator * 10000.0) / 100.0;
    }
}
