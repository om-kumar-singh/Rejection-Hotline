package com.om.rejectionhotline.scheduler;

import com.om.rejectionhotline.entity.JobApplication;
import com.om.rejectionhotline.entity.enums.EmailStatus;
import com.om.rejectionhotline.repository.JobApplicationRepository;
import com.om.rejectionhotline.service.NotificationService;
import com.om.rejectionhotline.util.FollowUpRules;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Component
public class ReminderScheduler {

    private static final Logger log = LoggerFactory.getLogger(ReminderScheduler.class);

    private final JobApplicationRepository jobApplicationRepository;
    private final NotificationService notificationService;

    public ReminderScheduler(JobApplicationRepository jobApplicationRepository,
                             NotificationService notificationService) {
        this.jobApplicationRepository = jobApplicationRepository;
        this.notificationService = notificationService;
    }

    @Scheduled(cron = "${app.scheduler.follow-up-cron:0 0 8 * * *}", zone = "${app.scheduler.timezone:Asia/Kolkata}")
    @Transactional
    public void runDailyFollowUpCheck() {
        LocalDate threshold = FollowUpRules.getFollowUpThresholdDate(LocalDate.now());
        List<JobApplication> eligible = jobApplicationRepository.findAllEligibleForFollowUp(
                EmailStatus.SENT, threshold);

        int created = 0;
        for (JobApplication application : eligible) {
            notificationService.createFollowUpNotification(application.getUser(), application);
            created++;
        }

        log.info("Follow-up scheduler completed. Eligible applications: {}, notifications processed: {}",
                eligible.size(), created);
    }
}
