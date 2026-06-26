package com.om.rejectionhotline.util;

import java.time.LocalDate;

public final class FollowUpRules {

    public static final int FOLLOW_UP_DAYS = 4;

    private FollowUpRules() {
    }

    public static LocalDate getFollowUpThresholdDate(LocalDate today) {
        return today.minusDays(FOLLOW_UP_DAYS);
    }
}
