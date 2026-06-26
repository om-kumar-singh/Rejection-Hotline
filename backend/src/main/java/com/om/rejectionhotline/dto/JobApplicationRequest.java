package com.om.rejectionhotline.dto;

import com.om.rejectionhotline.entity.enums.ApplicationStatus;
import com.om.rejectionhotline.entity.enums.EmailStatus;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JobApplicationRequest {

    private String hrName;

    @Email
    private String hrEmail;

    @NotBlank
    private String companyName;

    @NotBlank
    private String jobRole;

    @NotNull
    private LocalDate appliedDate;

    @NotNull
    private EmailStatus emailStatus;

    private boolean replyReceived;

    private ApplicationStatus applicationStatus;

    private String notes;
}
