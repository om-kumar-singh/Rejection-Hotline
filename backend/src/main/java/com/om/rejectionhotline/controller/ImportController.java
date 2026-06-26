package com.om.rejectionhotline.controller;

import com.om.rejectionhotline.dto.GoogleSheetsImportRequest;
import com.om.rejectionhotline.dto.ImportBatchResponse;
import com.om.rejectionhotline.dto.ImportPreviewResponse;
import com.om.rejectionhotline.service.ImportService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/import")
public class ImportController {

    private final ImportService importService;

    public ImportController(ImportService importService) {
        this.importService = importService;
    }

    @PostMapping("/excel/preview")
    public ResponseEntity<ImportPreviewResponse> previewExcel(@RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok(importService.previewExcel(file));
    }

    @PostMapping("/excel")
    public ResponseEntity<ImportBatchResponse> importExcel(@RequestParam("file") MultipartFile file) {
        return ResponseEntity.status(HttpStatus.CREATED).body(importService.importExcel(file));
    }

    @GetMapping("/google/auth-url")
    public ResponseEntity<Map<String, String>> googleAuthUrl() {
        return ResponseEntity.ok(Map.of("authUrl", importService.getGoogleAuthUrl()));
    }

    @GetMapping("/google/callback")
    public ResponseEntity<Map<String, String>> googleCallback(
            @RequestParam String code,
            @RequestParam String state) {
        importService.handleGoogleCallback(code, Long.parseLong(state));
        return ResponseEntity.ok(Map.of("message", "Google account connected successfully"));
    }

    @PostMapping("/google-sheets")
    public ResponseEntity<ImportBatchResponse> importGoogleSheets(
            @Valid @RequestBody GoogleSheetsImportRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(importService.importGoogleSheet(request.getSpreadsheetId(), request.getRange()));
    }
}
