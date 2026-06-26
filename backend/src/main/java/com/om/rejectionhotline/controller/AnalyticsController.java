package com.om.rejectionhotline.controller;

import com.om.rejectionhotline.dto.AnalyticsRatesResponse;
import com.om.rejectionhotline.dto.CompanyApplicationsResponse;
import com.om.rejectionhotline.dto.MonthlyApplicationsResponse;
import com.om.rejectionhotline.service.AnalyticsService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/v1/analytics")
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    public AnalyticsController(AnalyticsService analyticsService) {
        this.analyticsService = analyticsService;
    }

    @GetMapping("/applications-by-month")
    public ResponseEntity<MonthlyApplicationsResponse> applicationsByMonth(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        return ResponseEntity.ok(analyticsService.getApplicationsByMonth(from, to));
    }

    @GetMapping("/by-company")
    public ResponseEntity<CompanyApplicationsResponse> byCompany(
            @RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(analyticsService.getApplicationsByCompany(limit));
    }

    @GetMapping("/rates")
    public ResponseEntity<AnalyticsRatesResponse> rates(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        return ResponseEntity.ok(analyticsService.getRates(from, to));
    }
}
