package com.om.rejectionhotline.controller;

import com.om.rejectionhotline.dto.JobApplicationRequest;
import com.om.rejectionhotline.dto.JobApplicationResponse;
import com.om.rejectionhotline.dto.PageResponse;
import com.om.rejectionhotline.entity.enums.ApplicationFilter;
import com.om.rejectionhotline.service.JobApplicationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/applications")
public class JobApplicationController {

    private final JobApplicationService jobApplicationService;

    public JobApplicationController(JobApplicationService jobApplicationService) {
        this.jobApplicationService = jobApplicationService;
    }

    @PostMapping
    public ResponseEntity<JobApplicationResponse> create(@Valid @RequestBody JobApplicationRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(jobApplicationService.create(request));
    }

    @GetMapping
    public ResponseEntity<PageResponse<JobApplicationResponse>> list(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) ApplicationFilter filter,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String sort) {
        return ResponseEntity.ok(jobApplicationService.list(search, filter, page, size, sort));
    }

    @GetMapping("/{id}")
    public ResponseEntity<JobApplicationResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(jobApplicationService.getById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<JobApplicationResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody JobApplicationRequest request) {
        return ResponseEntity.ok(jobApplicationService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        jobApplicationService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/mark-follow-up-sent")
    public ResponseEntity<JobApplicationResponse> markFollowUpSent(@PathVariable Long id) {
        return ResponseEntity.ok(jobApplicationService.markFollowUpSent(id));
    }

    @PostMapping("/{id}/reset-follow-up")
    public ResponseEntity<JobApplicationResponse> resetFollowUp(@PathVariable Long id) {
        return ResponseEntity.ok(jobApplicationService.resetFollowUp(id));
    }
}
