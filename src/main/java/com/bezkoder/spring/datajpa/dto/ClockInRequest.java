package com.bezkoder.spring.datajpa.dto;

import lombok.Data;

@Data
public class ClockInRequest {
    private Long workerId;
    private Long siteId;
}