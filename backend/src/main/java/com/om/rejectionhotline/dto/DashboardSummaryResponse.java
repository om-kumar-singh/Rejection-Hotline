package com.om.rejectionhotline.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DashboardSummaryResponse {

    private long totalApplications;
    private long appliedToday;
    private long needFollowUp;
    private long interviews;
    private long rejected;
    private long offers;
    private double responseRate;
    private double followUpSuccessRate;
}
