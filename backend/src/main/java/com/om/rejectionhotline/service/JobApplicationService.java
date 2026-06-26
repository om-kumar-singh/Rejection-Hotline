package com.om.rejectionhotline.service;

import com.om.rejectionhotline.dto.JobApplicationRequest;
import com.om.rejectionhotline.dto.JobApplicationResponse;
import com.om.rejectionhotline.dto.PageResponse;
import com.om.rejectionhotline.entity.JobApplication;
import com.om.rejectionhotline.entity.User;
import com.om.rejectionhotline.entity.enums.ApplicationFilter;
import com.om.rejectionhotline.entity.enums.ImportSource;
import com.om.rejectionhotline.entity.enums.NotificationType;
import com.om.rejectionhotline.exception.ResourceNotFoundException;
import com.om.rejectionhotline.mapper.ApplicationMapper;
import com.om.rejectionhotline.repository.JobApplicationRepository;
import com.om.rejectionhotline.repository.NotificationRepository;
import com.om.rejectionhotline.repository.UserRepository;
import com.om.rejectionhotline.util.SecurityUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class JobApplicationService {

    private final JobApplicationRepository jobApplicationRepository;
    private final UserRepository userRepository;
    private final NotificationRepository notificationRepository;

    public JobApplicationService(JobApplicationRepository jobApplicationRepository,
                                 UserRepository userRepository,
                                 NotificationRepository notificationRepository) {
        this.jobApplicationRepository = jobApplicationRepository;
        this.userRepository = userRepository;
        this.notificationRepository = notificationRepository;
    }

    @Transactional
    public JobApplicationResponse create(JobApplicationRequest request) {
        Long userId = SecurityUtils.getCurrentUserId();
        User user = findUser(userId);
        JobApplication application = ApplicationMapper.toEntity(request, user, ImportSource.MANUAL);
        return ApplicationMapper.toResponse(jobApplicationRepository.save(application));
    }

    public PageResponse<JobApplicationResponse> list(String search, ApplicationFilter filter, int page, int size, String sort) {
        Long userId = SecurityUtils.getCurrentUserId();
        Sort sortOrder = Sort.by(Sort.Direction.DESC, sort != null && !sort.isBlank() ? sort : "appliedDate");
        Pageable pageable = PageRequest.of(page, size, sortOrder);
        Page<JobApplication> result = jobApplicationRepository.findAll(
                JobApplicationSpecifications.combine(userId, search, filter), pageable);

        List<JobApplicationResponse> content = result.getContent().stream()
                .map(ApplicationMapper::toResponse)
                .toList();

        return PageResponse.<JobApplicationResponse>builder()
                .content(content)
                .page(result.getNumber())
                .size(result.getSize())
                .totalElements(result.getTotalElements())
                .totalPages(result.getTotalPages())
                .last(result.isLast())
                .build();
    }

    public JobApplicationResponse getById(Long id) {
        Long userId = SecurityUtils.getCurrentUserId();
        JobApplication application = findApplication(id, userId);
        return ApplicationMapper.toResponse(application);
    }

    @Transactional
    public JobApplicationResponse update(Long id, JobApplicationRequest request) {
        Long userId = SecurityUtils.getCurrentUserId();
        JobApplication application = findApplication(id, userId);
        ApplicationMapper.updateEntity(application, request);
        return ApplicationMapper.toResponse(jobApplicationRepository.save(application));
    }

    @Transactional
    public void delete(Long id) {
        Long userId = SecurityUtils.getCurrentUserId();
        JobApplication application = findApplication(id, userId);
        jobApplicationRepository.delete(application);
    }

    @Transactional
    public JobApplicationResponse markFollowUpSent(Long id) {
        Long userId = SecurityUtils.getCurrentUserId();
        JobApplication application = findApplication(id, userId);
        application.setFollowUpSentCount(1);
        application.setFollowUpReminderEnabled(false);

        notificationRepository.findByUserIdOrderByReadAscCreatedAtDesc(userId, Pageable.unpaged())
                .getContent().stream()
                .filter(n -> n.getJobApplication() != null
                        && n.getJobApplication().getId().equals(id)
                        && n.getType() == NotificationType.FOLLOW_UP_DUE
                        && !n.isRead())
                .forEach(n -> n.setRead(true));

        return ApplicationMapper.toResponse(jobApplicationRepository.save(application));
    }

    @Transactional
    public JobApplicationResponse resetFollowUp(Long id) {
        Long userId = SecurityUtils.getCurrentUserId();
        JobApplication application = findApplication(id, userId);
        application.setFollowUpSentCount(0);
        application.setFollowUpReminderEnabled(true);
        return ApplicationMapper.toResponse(jobApplicationRepository.save(application));
    }

    @Transactional
    public JobApplication saveForImport(User user, JobApplicationRequest request, ImportSource source) {
        JobApplication application = ApplicationMapper.toEntity(request, user, source);
        return jobApplicationRepository.save(application);
    }

    public boolean isDuplicate(Long userId, JobApplicationRequest request) {
        return jobApplicationRepository.existsByUserIdAndCompanyNameIgnoreCaseAndJobRoleIgnoreCaseAndAppliedDate(
                userId, request.getCompanyName(), request.getJobRole(), request.getAppliedDate());
    }

    private JobApplication findApplication(Long id, Long userId) {
        return jobApplicationRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Application not found with id: " + id));
    }

    private User findUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }
}
