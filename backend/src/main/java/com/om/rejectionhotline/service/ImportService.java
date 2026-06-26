package com.om.rejectionhotline.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.om.rejectionhotline.dto.ImportBatchResponse;
import com.om.rejectionhotline.dto.ImportPreviewResponse;
import com.om.rejectionhotline.dto.JobApplicationRequest;
import com.om.rejectionhotline.entity.ImportBatch;
import com.om.rejectionhotline.entity.User;
import com.om.rejectionhotline.entity.enums.ImportBatchStatus;
import com.om.rejectionhotline.entity.enums.ImportSource;
import com.om.rejectionhotline.exception.ResourceNotFoundException;
import com.om.rejectionhotline.imports.ExcelImportParser;
import com.om.rejectionhotline.imports.GoogleSheetsClient;
import com.om.rejectionhotline.repository.ImportBatchRepository;
import com.om.rejectionhotline.repository.UserRepository;
import com.om.rejectionhotline.util.SecurityUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class ImportService {

    private final ExcelImportParser excelImportParser;
    private final GoogleSheetsClient googleSheetsClient;
    private final JobApplicationService jobApplicationService;
    private final NotificationService notificationService;
    private final ImportBatchRepository importBatchRepository;
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;

    public ImportService(ExcelImportParser excelImportParser,
                         GoogleSheetsClient googleSheetsClient,
                         JobApplicationService jobApplicationService,
                         NotificationService notificationService,
                         ImportBatchRepository importBatchRepository,
                         UserRepository userRepository,
                         ObjectMapper objectMapper) {
        this.excelImportParser = excelImportParser;
        this.googleSheetsClient = googleSheetsClient;
        this.jobApplicationService = jobApplicationService;
        this.notificationService = notificationService;
        this.importBatchRepository = importBatchRepository;
        this.userRepository = userRepository;
        this.objectMapper = objectMapper;
    }

    public ImportPreviewResponse previewExcel(MultipartFile file) {
        ExcelImportParser.ParsedImportResult result = excelImportParser.parse(file);
        return buildPreview(result.validApplications(), result.rowErrors());
    }

    @Transactional
    public ImportBatchResponse importExcel(MultipartFile file) {
        ExcelImportParser.ParsedImportResult result = excelImportParser.parse(file);
        return persistImport(result.validApplications(), result.rowErrors(), ImportSource.EXCEL);
    }

    @Transactional
    public ImportBatchResponse importGoogleSheet(String spreadsheetId, String range) {
        Long userId = SecurityUtils.getCurrentUserId();
        List<JobApplicationRequest> applications = googleSheetsClient.importSheet(userId, spreadsheetId, range);
        return persistImport(applications, Map.of(), ImportSource.GOOGLE_SHEETS);
    }

    public String getGoogleAuthUrl() {
        return googleSheetsClient.buildAuthorizationUrl(SecurityUtils.getCurrentUserId());
    }

    @Transactional
    public void handleGoogleCallback(String code, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        googleSheetsClient.handleOAuthCallback(code, userId, user);
    }

    private ImportBatchResponse persistImport(List<JobApplicationRequest> applications,
                                              Map<Integer, String> rowErrors,
                                              ImportSource source) {
        Long userId = SecurityUtils.getCurrentUserId();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        int success = 0;
        int failure = rowErrors.size();
        List<String> duplicateErrors = new ArrayList<>();

        for (JobApplicationRequest request : applications) {
            if (jobApplicationService.isDuplicate(userId, request)) {
                failure++;
                duplicateErrors.add("Duplicate: " + request.getCompanyName() + " - " + request.getJobRole());
                continue;
            }
            jobApplicationService.saveForImport(user, request, source);
            success++;
        }

        ImportBatchStatus status = failure == 0 ? ImportBatchStatus.COMPLETED
                : success == 0 ? ImportBatchStatus.FAILED : ImportBatchStatus.PARTIAL;

        ImportBatch batch = ImportBatch.builder()
                .user(user)
                .source(source)
                .totalRows(applications.size() + rowErrors.size())
                .successCount(success)
                .failureCount(failure)
                .status(status)
                .errorReportJson(toJson(rowErrors, duplicateErrors))
                .build();

        ImportBatch saved = importBatchRepository.save(batch);
        if (success > 0) {
            notificationService.createImportCompleteNotification(user, success);
        }

        return toBatchResponse(saved);
    }

    private ImportPreviewResponse buildPreview(List<JobApplicationRequest> valid, Map<Integer, String> errors) {
        return ImportPreviewResponse.builder()
                .totalRows(valid.size() + errors.size())
                .validRows(valid.size())
                .invalidRows(errors.size())
                .validApplications(valid)
                .rowErrors(errors)
                .build();
    }

    private ImportBatchResponse toBatchResponse(ImportBatch batch) {
        return ImportBatchResponse.builder()
                .id(batch.getId())
                .source(batch.getSource())
                .totalRows(batch.getTotalRows())
                .successCount(batch.getSuccessCount())
                .failureCount(batch.getFailureCount())
                .status(batch.getStatus())
                .errorReportJson(batch.getErrorReportJson())
                .createdAt(batch.getCreatedAt())
                .build();
    }

    private String toJson(Map<Integer, String> rowErrors, List<String> duplicateErrors) {
        try {
            return objectMapper.writeValueAsString(Map.of(
                    "rowErrors", rowErrors,
                    "duplicateErrors", duplicateErrors
            ));
        } catch (JsonProcessingException ex) {
            return "{}";
        }
    }
}
