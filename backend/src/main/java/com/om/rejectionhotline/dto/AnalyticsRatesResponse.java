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
public class AnalyticsRatesResponse {

    private double responseRate;
    private double followUpRate;
    private double interviewRate;
    private double offerRate;
}
