package com.om.rejectionhotline.service;

import com.om.rejectionhotline.entity.JobApplication;
import com.om.rejectionhotline.entity.enums.ApplicationStatus;
import com.om.rejectionhotline.entity.enums.EmailStatus;
import com.om.rejectionhotline.util.FollowUpRules;

import java.time.LocalDate;

public final class FollowUpReminderEngine {

    private FollowUpReminderEngine() {
    }

    public static boolean isEligible(JobApplication application) {
        LocalDate today = LocalDate.now();
        LocalDate threshold = FollowUpRules.getFollowUpThresholdDate(today);
        return application.getEmailStatus() == EmailStatus.SENT
                && !application.isReplyReceived()
                && application.getFollowUpSentCount() == 0
                && application.isFollowUpReminderEnabled()
                && !application.getAppliedDate().isAfter(threshold);
    }

    public static boolean isWaiting(JobApplication application) {
        return application.getEmailStatus() == EmailStatus.SENT
                && !application.isReplyReceived()
                && application.getApplicationStatus() != ApplicationStatus.REJECTED
                && application.getApplicationStatus() != ApplicationStatus.OFFER
                && !isEligible(application);
    }
}
