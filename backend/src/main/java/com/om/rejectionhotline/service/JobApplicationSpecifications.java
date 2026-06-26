package com.om.rejectionhotline.service;

import com.om.rejectionhotline.entity.JobApplication;
import com.om.rejectionhotline.entity.enums.ApplicationFilter;
import com.om.rejectionhotline.entity.enums.ApplicationStatus;
import com.om.rejectionhotline.entity.enums.EmailStatus;
import com.om.rejectionhotline.util.FollowUpRules;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public final class JobApplicationSpecifications {

    private JobApplicationSpecifications() {
    }

    public static Specification<JobApplication> forUser(Long userId) {
        return (root, query, cb) -> cb.equal(root.get("user").get("id"), userId);
    }

    public static Specification<JobApplication> search(String search) {
        if (search == null || search.isBlank()) {
            return null;
        }
        String pattern = "%" + search.trim().toLowerCase() + "%";
        return (root, query, cb) -> cb.or(
                cb.like(cb.lower(root.get("companyName")), pattern),
                cb.like(cb.lower(root.get("hrName")), pattern),
                cb.like(cb.lower(root.get("hrEmail")), pattern),
                cb.like(cb.lower(root.get("jobRole")), pattern)
        );
    }

    public static Specification<JobApplication> filter(ApplicationFilter filter) {
        if (filter == null) {
            return null;
        }
        LocalDate today = LocalDate.now();
        LocalDate threshold = FollowUpRules.getFollowUpThresholdDate(today);

        return switch (filter) {
            case APPLIED_TODAY -> (root, query, cb) -> cb.equal(root.get("appliedDate"), today);
            case APPLIED_THIS_WEEK -> {
                LocalDate startOfWeek = today.with(DayOfWeek.MONDAY);
                yield (root, query, cb) -> cb.between(root.get("appliedDate"), startOfWeek, today);
            }
            case NEED_FOLLOW_UP -> (root, query, cb) -> cb.and(
                    cb.equal(root.get("emailStatus"), EmailStatus.SENT),
                    cb.isFalse(root.get("replyReceived")),
                    cb.equal(root.get("followUpSentCount"), 0),
                    cb.isTrue(root.get("followUpReminderEnabled")),
                    cb.lessThanOrEqualTo(root.get("appliedDate"), threshold)
            );
            case WAITING -> (root, query, cb) -> cb.and(
                    cb.equal(root.get("emailStatus"), EmailStatus.SENT),
                    cb.isFalse(root.get("replyReceived")),
                    cb.not(root.get("applicationStatus").in(ApplicationStatus.REJECTED, ApplicationStatus.OFFER)),
                    cb.or(
                            cb.greaterThan(root.get("appliedDate"), threshold),
                            cb.greaterThan(root.get("followUpSentCount"), 0),
                            cb.isFalse(root.get("followUpReminderEnabled"))
                    )
            );
            case INTERVIEW -> (root, query, cb) -> cb.equal(root.get("applicationStatus"), ApplicationStatus.INTERVIEW);
            case REJECTED -> (root, query, cb) -> cb.equal(root.get("applicationStatus"), ApplicationStatus.REJECTED);
            case OFFER -> (root, query, cb) -> cb.equal(root.get("applicationStatus"), ApplicationStatus.OFFER);
        };
    }

    public static Specification<JobApplication> combine(Long userId, String search, ApplicationFilter filter) {
        List<Specification<JobApplication>> specs = new ArrayList<>();
        specs.add(forUser(userId));
        Specification<JobApplication> searchSpec = search(search);
        if (searchSpec != null) {
            specs.add(searchSpec);
        }
        Specification<JobApplication> filterSpec = filter(filter);
        if (filterSpec != null) {
            specs.add(filterSpec);
        }
        return specs.stream().reduce(Specification::and).orElse(forUser(userId));
    }
}
