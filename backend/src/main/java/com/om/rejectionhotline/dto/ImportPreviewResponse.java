package com.om.rejectionhotline.dto;

import com.om.rejectionhotline.entity.enums.ImportBatchStatus;
import com.om.rejectionhotline.entity.enums.ImportSource;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ImportPreviewResponse {

    private int totalRows;
    private int validRows;
    private int invalidRows;
    private List<JobApplicationRequest> validApplications;
    private Map<Integer, String> rowErrors;
}
