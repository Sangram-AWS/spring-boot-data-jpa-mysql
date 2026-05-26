package com.bezkoder.spring.datajpa.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OvertimeSummaryDto {
    private Long workerId;
    private String workerName;
    private String month;
    private BigDecimal totalOvertimeHours;
    private BigDecimal totalPayoutAmount;
    private String settlementStatus;
    private List<OvertimeDayDetail> breakdown;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OvertimeDayDetail {
        private LocalDate date;
        private BigDecimal overtimeHours;
        private BigDecimal amount;
        private String status;
    }
}