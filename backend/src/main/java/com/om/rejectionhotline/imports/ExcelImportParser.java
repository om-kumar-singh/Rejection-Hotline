package com.om.rejectionhotline.imports;

import com.om.rejectionhotline.dto.JobApplicationRequest;
import com.om.rejectionhotline.entity.enums.ApplicationStatus;
import com.om.rejectionhotline.entity.enums.EmailStatus;
import com.om.rejectionhotline.exception.BadRequestException;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

@Component
public class ExcelImportParser {

    private static final List<String> EXPECTED_HEADERS = List.of(
            "HR Name", "HR Email", "Company Name", "Job Role", "Applied Date",
            "Email Status", "Reply Received", "Follow-up Sent Count", "Notes"
    );

    public ParsedImportResult parse(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BadRequestException("Excel file is required");
        }

        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rows = sheet.iterator();

            if (!rows.hasNext()) {
                throw new BadRequestException("Excel sheet is empty");
            }

            Row headerRow = rows.next();
            validateHeaders(headerRow);

            List<JobApplicationRequest> validApplications = new ArrayList<>();
            Map<Integer, String> rowErrors = new LinkedHashMap<>();
            int rowNum = 1;

            while (rows.hasNext()) {
                rowNum++;
                Row row = rows.next();
                if (isEmptyRow(row)) {
                    continue;
                }
                try {
                    validApplications.add(parseRow(row));
                } catch (Exception ex) {
                    rowErrors.put(rowNum, ex.getMessage());
                }
            }

            return new ParsedImportResult(validApplications, rowErrors);
        } catch (IOException ex) {
            throw new BadRequestException("Failed to read Excel file: " + ex.getMessage());
        }
    }

    private void validateHeaders(Row headerRow) {
        for (int i = 0; i < EXPECTED_HEADERS.size(); i++) {
            String expected = EXPECTED_HEADERS.get(i);
            String actual = getCellString(headerRow.getCell(i));
            if (actual == null || !actual.equalsIgnoreCase(expected)) {
                throw new BadRequestException("Invalid header at column " + (i + 1)
                        + ". Expected '" + expected + "' but found '" + actual + "'");
            }
        }
    }

    private JobApplicationRequest parseRow(Row row) {
        String companyName = requireCell(row, 2, "Company Name");
        String jobRole = requireCell(row, 3, "Job Role");
        LocalDate appliedDate = parseDate(row.getCell(4));
        EmailStatus emailStatus = parseEmailStatus(requireCell(row, 5, "Email Status"));
        boolean replyReceived = parseBoolean(getCellString(row.getCell(6)));

        ApplicationStatus status = ApplicationStatus.APPLIED;
        if (replyReceived) {
            status = ApplicationStatus.WAITING;
        }

        return JobApplicationRequest.builder()
                .hrName(getCellString(row.getCell(0)))
                .hrEmail(getCellString(row.getCell(1)))
                .companyName(companyName)
                .jobRole(jobRole)
                .appliedDate(appliedDate)
                .emailStatus(emailStatus)
                .replyReceived(replyReceived)
                .applicationStatus(status)
                .notes(getCellString(row.getCell(8)))
                .build();
    }

    private String requireCell(Row row, int index, String field) {
        String value = getCellString(row.getCell(index));
        if (value == null || value.isBlank()) {
            throw new BadRequestException(field + " is required");
        }
        return value.trim();
    }

    private LocalDate parseDate(Cell cell) {
        if (cell == null) {
            throw new BadRequestException("Applied Date is required");
        }
        if (cell.getCellType() == CellType.NUMERIC && DateUtil.isCellDateFormatted(cell)) {
            return cell.getDateCellValue().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        }
        String value = getCellString(cell);
        if (value == null || value.isBlank()) {
            throw new BadRequestException("Applied Date is required");
        }
        return LocalDate.parse(value.trim());
    }

    private EmailStatus parseEmailStatus(String value) {
        try {
            return EmailStatus.valueOf(value.trim().toUpperCase());
        } catch (Exception ex) {
            throw new BadRequestException("Invalid Email Status: " + value);
        }
    }

    private boolean parseBoolean(String value) {
        if (value == null || value.isBlank()) {
            return false;
        }
        return value.trim().equalsIgnoreCase("yes")
                || value.trim().equalsIgnoreCase("true")
                || value.trim().equals("1");
    }

    private String getCellString(Cell cell) {
        if (cell == null) {
            return null;
        }
        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue();
            case NUMERIC -> DateUtil.isCellDateFormatted(cell)
                    ? cell.getLocalDateTimeCellValue().toLocalDate().toString()
                    : String.valueOf((long) cell.getNumericCellValue());
            case BOOLEAN -> String.valueOf(cell.getBooleanCellValue());
            default -> null;
        };
    }

    private boolean isEmptyRow(Row row) {
        for (int i = 0; i <= 8; i++) {
            String value = getCellString(row.getCell(i));
            if (value != null && !value.isBlank()) {
                return false;
            }
        }
        return true;
    }

    public record ParsedImportResult(
            List<JobApplicationRequest> validApplications,
            Map<Integer, String> rowErrors
    ) {
    }
}
