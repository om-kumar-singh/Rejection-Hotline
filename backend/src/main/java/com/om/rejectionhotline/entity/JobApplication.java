package com.om.rejectionhotline.entity;

import com.om.rejectionhotline.entity.enums.ApplicationStatus;
import com.om.rejectionhotline.entity.enums.EmailStatus;
import com.om.rejectionhotline.entity.enums.ImportSource;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "job_applications")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JobApplication {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "hr_name", length = 100)
    private String hrName;

    @Column(name = "hr_email")
    private String hrEmail;

    @Column(name = "company_name", nullable = false, length = 150)
    private String companyName;

    @Column(name = "job_role", nullable = false, length = 150)
    private String jobRole;

    @Column(name = "applied_date", nullable = false)
    private LocalDate appliedDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "email_status", nullable = false, length = 20)
    private EmailStatus emailStatus;

    @Column(name = "reply_received", nullable = false)
    @Builder.Default
    private boolean replyReceived = false;

    @Column(name = "follow_up_sent_count", nullable = false)
    @Builder.Default
    private int followUpSentCount = 0;

    @Column(name = "follow_up_reminder_enabled", nullable = false)
    @Builder.Default
    private boolean followUpReminderEnabled = true;

    @Enumerated(EnumType.STRING)
    @Column(name = "application_status", nullable = false, length = 20)
    @Builder.Default
    private ApplicationStatus applicationStatus = ApplicationStatus.APPLIED;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Enumerated(EnumType.STRING)
    @Column(name = "import_source", nullable = false, length = 20)
    @Builder.Default
    private ImportSource importSource = ImportSource.MANUAL;

    @Version
    private int version;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}
