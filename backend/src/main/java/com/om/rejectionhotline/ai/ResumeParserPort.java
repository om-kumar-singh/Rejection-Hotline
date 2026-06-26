package com.om.rejectionhotline.ai;

/**
 * Future AI module boundary — implementations can be added without changing core domain.
 */
public interface ResumeParserPort {
    String parseResume(byte[] fileContent, String filename);
}
