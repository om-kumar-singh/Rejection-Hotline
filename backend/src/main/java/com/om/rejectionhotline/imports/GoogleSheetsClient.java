package com.om.rejectionhotline.imports;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.ValueRange;
import com.om.rejectionhotline.config.GoogleProperties;
import com.om.rejectionhotline.dto.JobApplicationRequest;
import com.om.rejectionhotline.entity.GoogleOAuthToken;
import com.om.rejectionhotline.entity.User;
import com.om.rejectionhotline.entity.enums.ApplicationStatus;
import com.om.rejectionhotline.entity.enums.EmailStatus;
import com.om.rejectionhotline.exception.BadRequestException;
import com.om.rejectionhotline.repository.GoogleOAuthTokenRepository;
import com.om.rejectionhotline.service.TokenEncryptionService;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.util.store.MemoryDataStoreFactory;
import org.springframework.stereotype.Component;

import java.io.StringReader;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Component
public class GoogleSheetsClient {

    private static final List<String> SCOPES = List.of("https://www.googleapis.com/auth/spreadsheets.readonly");

    private final GoogleProperties googleProperties;
    private final GoogleOAuthTokenRepository googleOAuthTokenRepository;
    private final TokenEncryptionService tokenEncryptionService;
    private final ObjectMapper objectMapper;

    public GoogleSheetsClient(GoogleProperties googleProperties,
                              GoogleOAuthTokenRepository googleOAuthTokenRepository,
                              TokenEncryptionService tokenEncryptionService,
                              ObjectMapper objectMapper) {
        this.googleProperties = googleProperties;
        this.googleOAuthTokenRepository = googleOAuthTokenRepository;
        this.tokenEncryptionService = tokenEncryptionService;
        this.objectMapper = objectMapper;
    }

    public String buildAuthorizationUrl(Long userId) {
        try {
            GoogleAuthorizationCodeFlow flow = buildFlow();
            return flow.newAuthorizationUrl()
                    .setRedirectUri(googleProperties.getRedirectUri())
                    .setState(String.valueOf(userId))
                    .build();
        } catch (Exception ex) {
            throw new BadRequestException("Google OAuth is not configured: " + ex.getMessage());
        }
    }

    public void handleOAuthCallback(String code, Long userId, User user) {
        try {
            GoogleAuthorizationCodeFlow flow = buildFlow();
            GoogleTokenResponse response = flow.newTokenRequest(code)
                    .setRedirectUri(googleProperties.getRedirectUri())
                    .execute();

            String encrypted = tokenEncryptionService.encrypt(response.getRefreshToken());
            GoogleOAuthToken token = googleOAuthTokenRepository.findByUserId(userId)
                    .orElse(GoogleOAuthToken.builder().user(user).build());
            token.setEncryptedRefreshToken(encrypted);
            token.setScopes(String.join(",", SCOPES));
            googleOAuthTokenRepository.save(token);
        } catch (Exception ex) {
            throw new BadRequestException("Failed to complete Google OAuth: " + ex.getMessage());
        }
    }

    public List<JobApplicationRequest> importSheet(Long userId, String spreadsheetId, String range) {
        if (googleProperties.getClientId() == null || googleProperties.getClientId().isBlank()) {
            throw new BadRequestException("Google OAuth credentials are not configured");
        }

        GoogleOAuthToken token = googleOAuthTokenRepository.findByUserId(userId)
                .orElseThrow(() -> new BadRequestException("Google account not connected. Complete OAuth first."));

        try {
            GoogleAuthorizationCodeFlow flow = buildFlow();
            String refreshToken = tokenEncryptionService.decrypt(token.getEncryptedRefreshToken());
            Credential credential = new Credential.Builder(flow.getMethod()).build()
                    .setRefreshToken(refreshToken);
            credential.refreshToken();

            Sheets sheets = new Sheets.Builder(
                    GoogleNetHttpTransport.newTrustedTransport(),
                    GsonFactory.getDefaultInstance(),
                    credential
            ).setApplicationName("Rejection Hotline").build();

            String sheetRange = range != null && !range.isBlank() ? range : "Sheet1!A2:I";
            ValueRange response = sheets.spreadsheets().values()
                    .get(spreadsheetId, sheetRange)
                    .execute();

            List<List<Object>> values = response.getValues();
            if (values == null || values.isEmpty()) {
                return Collections.emptyList();
            }

            List<JobApplicationRequest> applications = new ArrayList<>();
            for (List<Object> row : values) {
                if (row.isEmpty()) {
                    continue;
                }
                applications.add(parseRow(row));
            }
            return applications;
        } catch (Exception ex) {
            throw new BadRequestException("Failed to import Google Sheet: " + ex.getMessage());
        }
    }

    private JobApplicationRequest parseRow(List<Object> row) {
        String companyName = getValue(row, 2);
        String jobRole = getValue(row, 3);
        if (companyName.isBlank() || jobRole.isBlank()) {
            throw new BadRequestException("Company Name and Job Role are required");
        }

        return JobApplicationRequest.builder()
                .hrName(getValue(row, 0))
                .hrEmail(getValue(row, 1))
                .companyName(companyName)
                .jobRole(jobRole)
                .appliedDate(LocalDate.parse(getValue(row, 4)))
                .emailStatus(EmailStatus.valueOf(getValue(row, 5).toUpperCase()))
                .replyReceived(parseYesNo(getValue(row, 6)))
                .applicationStatus(ApplicationStatus.APPLIED)
                .notes(getValue(row, 8))
                .build();
    }

    private GoogleAuthorizationCodeFlow buildFlow() throws Exception {
        GoogleClientSecrets.Details details = new GoogleClientSecrets.Details();
        details.setClientId(googleProperties.getClientId());
        details.setClientSecret(googleProperties.getClientSecret());
        GoogleClientSecrets clientSecrets = new GoogleClientSecrets().setInstalled(details);

        return new GoogleAuthorizationCodeFlow.Builder(
                GoogleNetHttpTransport.newTrustedTransport(),
                GsonFactory.getDefaultInstance(),
                clientSecrets,
                SCOPES
        ).setDataStoreFactory(new MemoryDataStoreFactory()).build();
    }

    private String getValue(List<Object> row, int index) {
        if (index >= row.size() || row.get(index) == null) {
            return "";
        }
        return row.get(index).toString().trim();
    }

    private boolean parseYesNo(String value) {
        return value.equalsIgnoreCase("yes") || value.equalsIgnoreCase("true");
    }
}
