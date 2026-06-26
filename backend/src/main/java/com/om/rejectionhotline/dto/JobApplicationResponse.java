package com.om.rejectionhotline.dto;

import com.om.rejectionhotline.entity.enums.ApplicationStatus;
import com.om.rejectionhotline.entity.enums.EmailStatus;
import com.om.rejectionhotline.entity.enums.ImportSource;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JobApplicationResponse {

    private Long id;
    private String hrName;
    private String hrEmail;
    private String companyName;
    private String jobRole;
    private LocalDate appliedDate;
    private EmailStatus emailStatus;
    private boolean replyReceived;
    private int followUpSentCount;
    private boolean followUpReminderEnabled;
    private ApplicationStatus applicationStatus;
    private String notes;
    private ImportSource importSource;
    private boolean needFollowUp;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
