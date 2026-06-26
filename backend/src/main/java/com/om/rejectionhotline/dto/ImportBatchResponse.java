package com.om.rejectionhotline.dto;

import com.om.rejectionhotline.entity.enums.ImportBatchStatus;
import com.om.rejectionhotline.entity.enums.ImportSource;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ImportBatchResponse {

    private Long id;
    private ImportSource source;
    private int totalRows;
    private int successCount;
    private int failureCount;
    private ImportBatchStatus status;
    private String errorReportJson;
    private LocalDateTime createdAt;
}
