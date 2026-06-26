package com.om.rejectionhotline.service;

import com.om.rejectionhotline.dto.NotificationResponse;
import com.om.rejectionhotline.dto.PageResponse;
import com.om.rejectionhotline.entity.JobApplication;
import com.om.rejectionhotline.entity.Notification;
import com.om.rejectionhotline.entity.User;
import com.om.rejectionhotline.entity.enums.NotificationType;
import com.om.rejectionhotline.exception.ResourceNotFoundException;
import com.om.rejectionhotline.mapper.ApplicationMapper;
import com.om.rejectionhotline.repository.NotificationRepository;
import com.om.rejectionhotline.util.SecurityUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;

    public NotificationService(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    public PageResponse<NotificationResponse> list(int page, int size) {
        Long userId = SecurityUtils.getCurrentUserId();
        Pageable pageable = PageRequest.of(page, size);
        Page<Notification> result = notificationRepository.findByUserIdOrderByReadAscCreatedAtDesc(userId, pageable);

        List<NotificationResponse> content = result.getContent().stream()
                .map(ApplicationMapper::toNotificationResponse)
                .toList();

        return PageResponse.<NotificationResponse>builder()
                .content(content)
                .page(result.getNumber())
                .size(result.getSize())
                .totalElements(result.getTotalElements())
                .totalPages(result.getTotalPages())
                .last(result.isLast())
                .build();
    }

    public long getUnreadCount() {
        return notificationRepository.countByUserIdAndReadFalse(SecurityUtils.getCurrentUserId());
    }

    @Transactional
    public NotificationResponse markRead(Long id) {
        Long userId = SecurityUtils.getCurrentUserId();
        Notification notification = notificationRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found with id: " + id));
        notification.setRead(true);
        return ApplicationMapper.toNotificationResponse(notificationRepository.save(notification));
    }

    @Transactional
    public void markAllRead() {
        notificationRepository.markAllReadByUserId(SecurityUtils.getCurrentUserId());
    }

    @Transactional
    public void createFollowUpNotification(User user, JobApplication application) {
        if (notificationRepository.existsByJobApplicationIdAndTypeAndReadFalse(
                application.getId(), NotificationType.FOLLOW_UP_DUE)) {
            return;
        }

        Notification notification = Notification.builder()
                .user(user)
                .jobApplication(application)
                .type(NotificationType.FOLLOW_UP_DUE)
                .title("Follow-up due: " + application.getCompanyName())
                .message(String.format(
                        "It has been 4+ days since you applied to %s for %s. Consider sending a follow-up email to %s.",
                        application.getCompanyName(),
                        application.getJobRole(),
                        application.getHrName() != null ? application.getHrName() : "the HR contact"
                ))
                .read(false)
                .build();

        notificationRepository.save(notification);
    }

    @Transactional
    public void createImportCompleteNotification(User user, int successCount) {
        Notification notification = Notification.builder()
                .user(user)
                .type(NotificationType.IMPORT_COMPLETE)
                .title("Import completed")
                .message(successCount + " applications imported successfully.")
                .read(false)
                .build();
        notificationRepository.save(notification);
    }
}
