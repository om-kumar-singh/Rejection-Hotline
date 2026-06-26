package com.om.rejectionhotline;

import com.om.rejectionhotline.entity.enums.EmailStatus;
import com.om.rejectionhotline.service.FollowUpReminderEngine;
import com.om.rejectionhotline.entity.JobApplication;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class FollowUpReminderEngineTest {

    @Test
    void isEligible_whenAllConditionsMet_returnsTrue() {
        JobApplication app = JobApplication.builder()
                .emailStatus(EmailStatus.SENT)
                .replyReceived(false)
                .followUpSentCount(0)
                .followUpReminderEnabled(true)
                .appliedDate(LocalDate.now().minusDays(5))
                .build();

        assertTrue(FollowUpReminderEngine.isEligible(app));
    }

    @Test
    void isEligible_whenFollowUpAlreadySent_returnsFalse() {
        JobApplication app = JobApplication.builder()
                .emailStatus(EmailStatus.SENT)
                .replyReceived(false)
                .followUpSentCount(1)
                .followUpReminderEnabled(false)
                .appliedDate(LocalDate.now().minusDays(5))
                .build();

        assertFalse(FollowUpReminderEngine.isEligible(app));
    }

    @Test
    void isEligible_whenAppliedTooRecently_returnsFalse() {
        JobApplication app = JobApplication.builder()
                .emailStatus(EmailStatus.SENT)
                .replyReceived(false)
                .followUpSentCount(0)
                .followUpReminderEnabled(true)
                .appliedDate(LocalDate.now().minusDays(2))
                .build();

        assertFalse(FollowUpReminderEngine.isEligible(app));
    }
}
