package com.om.rejectionhotline.repository;

import com.om.rejectionhotline.entity.GoogleOAuthToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GoogleOAuthTokenRepository extends JpaRepository<GoogleOAuthToken, Long> {

    Optional<GoogleOAuthToken> findByUserId(Long userId);
}
