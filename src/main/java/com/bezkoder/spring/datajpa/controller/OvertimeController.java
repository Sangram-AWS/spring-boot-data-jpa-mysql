package com.bezkoder.spring.datajpa.controller;

import com.bezkoder.spring.datajpa.dto.OvertimeSummaryDto;
import com.bezkoder.spring.datajpa.service.OvertimeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Map;

@RestController
@RequestMapping("/api/overtime")
@RequiredArgsConstructor
public class OvertimeController {

    private final OvertimeService overtimeService;

    @GetMapping("/summary/{workerId}")
    public ResponseEntity<OvertimeSummaryDto> getOvertimeSummary(
            @PathVariable Long workerId,
            @RequestParam String month) {
        return ResponseEntity.ok(overtimeService.getOvertimeSummary(workerId, month));
    }

    @PostMapping("/settle/{workerId}")
    public ResponseEntity<Map<String, Object>> settleOvertime(
            @PathVariable Long workerId,
            @RequestParam String month) {
        BigDecimal totalAmount = overtimeService.settleOvertime(workerId, month);
        return ResponseEntity.ok(Map.of(
                "message", "Overtime settled successfully",
                "workerId", workerId,
                "month", month,
                "totalAmount", totalAmount
        ));
    }
}