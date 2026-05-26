package com.bezkoder.spring.datajpa.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ActiveWorkerDto {
    private Long workerId;
    private String workerName;
    private String designation;
    private Long siteId;
    private String siteName;
    private LocalDateTime clockInTime;
}