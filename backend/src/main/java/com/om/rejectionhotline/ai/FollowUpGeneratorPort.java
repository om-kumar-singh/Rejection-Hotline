package com.om.rejectionhotline.ai;

public interface FollowUpGeneratorPort {
    String generateFollowUpEmail(Long applicationId, Long userId);
}
