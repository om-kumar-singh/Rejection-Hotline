package com.om.rejectionhotline.mapper;

import com.om.rejectionhotline.dto.JobApplicationRequest;
import com.om.rejectionhotline.dto.JobApplicationResponse;
import com.om.rejectionhotline.dto.NotificationResponse;
import com.om.rejectionhotline.dto.UserResponse;
import com.om.rejectionhotline.entity.JobApplication;
import com.om.rejectionhotline.entity.Notification;
import com.om.rejectionhotline.entity.User;
import com.om.rejectionhotline.entity.enums.ApplicationStatus;
import com.om.rejectionhotline.entity.enums.ImportSource;
import com.om.rejectionhotline.service.FollowUpReminderEngine;

public final class ApplicationMapper {

    private ApplicationMapper() {
    }

    public static JobApplication toEntity(JobApplicationRequest request, User user, ImportSource importSource) {
        return JobApplication.builder()
                .user(user)
                .hrName(request.getHrName())
                .hrEmail(request.getHrEmail())
                .companyName(request.getCompanyName())
                .jobRole(request.getJobRole())
                .appliedDate(request.getAppliedDate())
                .emailStatus(request.getEmailStatus())
                .replyReceived(request.isReplyReceived())
                .applicationStatus(request.getApplicationStatus() != null
                        ? request.getApplicationStatus()
                        : ApplicationStatus.APPLIED)
                .notes(request.getNotes())
                .importSource(importSource)
                .followUpSentCount(0)
                .followUpReminderEnabled(true)
                .build();
    }

    public static void updateEntity(JobApplication application, JobApplicationRequest request) {
        application.setHrName(request.getHrName());
        application.setHrEmail(request.getHrEmail());
        application.setCompanyName(request.getCompanyName());
        application.setJobRole(request.getJobRole());
        application.setAppliedDate(request.getAppliedDate());
        application.setEmailStatus(request.getEmailStatus());
        application.setReplyReceived(request.isReplyReceived());
        if (request.getApplicationStatus() != null) {
            application.setApplicationStatus(request.getApplicationStatus());
        }
        application.setNotes(request.getNotes());
    }

    public static JobApplicationResponse toResponse(JobApplication application) {
        return JobApplicationResponse.builder()
                .id(application.getId())
                .hrName(application.getHrName())
                .hrEmail(application.getHrEmail())
                .companyName(application.getCompanyName())
                .jobRole(application.getJobRole())
                .appliedDate(application.getAppliedDate())
                .emailStatus(application.getEmailStatus())
                .replyReceived(application.isReplyReceived())
                .followUpSentCount(application.getFollowUpSentCount())
                .followUpReminderEnabled(application.isFollowUpReminderEnabled())
                .applicationStatus(application.getApplicationStatus())
                .notes(application.getNotes())
                .importSource(application.getImportSource())
                .needFollowUp(FollowUpReminderEngine.isEligible(application))
                .createdAt(application.getCreatedAt())
                .updatedAt(application.getUpdatedAt())
                .build();
    }

    public static UserResponse toUserResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .createdAt(user.getCreatedAt())
                .build();
    }

    public static NotificationResponse toNotificationResponse(Notification notification) {
        return NotificationResponse.builder()
                .id(notification.getId())
                .jobApplicationId(notification.getJobApplication() != null
                        ? notification.getJobApplication().getId()
                        : null)
                .type(notification.getType())
                .title(notification.getTitle())
                .message(notification.getMessage())
                .read(notification.isRead())
                .createdAt(notification.getCreatedAt())
                .build();
    }
}
