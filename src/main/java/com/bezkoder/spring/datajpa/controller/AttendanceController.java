package com.bezkoder.spring.datajpa.controller;

import com.bezkoder.spring.datajpa.entity.AttendanceLog;
import com.bezkoder.spring.datajpa.service.AttendanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.Map;

@RestController
@RequestMapping("/api/attendance")
@RequiredArgsConstructor
public class AttendanceController {

    private final AttendanceService attendanceService;

    @PostMapping("/clock-in")
    public ResponseEntity<AttendanceLog> clockIn(
            @RequestBody Map<String, Long> request
    ) {

        return ResponseEntity.ok(
                attendanceService.clockIn(
                        request.get("workerId"),
                        request.get("siteId")
                )
        );
    }

    @PostMapping("/clock-out")
    public ResponseEntity<AttendanceLog> clockOut(
            @RequestBody Map<String, Long> request
    ) {

        return ResponseEntity.ok(
                attendanceService.clockOut(
                        request.get("workerId")
                )
        );
    }

    @GetMapping("/active")
    public ResponseEntity<Collection<Object>>
    getActiveWorkers() {

        return ResponseEntity.ok(
                attendanceService.getActiveWorkers()
        );
    }

    @GetMapping("/log")
    public Page<AttendanceLog> getLogs(
            @RequestParam Long workerId,
            Pageable pageable
    ) {

        return attendanceService.getLogs(
                workerId,
                pageable
        );
    }
}