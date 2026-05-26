package com.bezkoder.spring.datajpa.service;

import com.bezkoder.spring.datajpa.entity.AttendanceLog;
import com.bezkoder.spring.datajpa.entity.Site;
import com.bezkoder.spring.datajpa.entity.Worker;
import com.bezkoder.spring.datajpa.repository.AttendanceLogRepository;
import com.bezkoder.spring.datajpa.repository.SiteRepository;
import com.bezkoder.spring.datajpa.repository.WorkerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class AttendanceService {

    private final AttendanceLogRepository attendanceLogRepository;

    private final WorkerRepository workerRepository;

    private final SiteRepository siteRepository;

    private final RedisTemplate<String, Object> redisTemplate;

    public AttendanceLog clockIn(
            Long workerId,
            Long siteId
    ) {

        Worker worker = workerRepository.findById(workerId)
                .orElseThrow(() ->
                        new RuntimeException("Worker not found")
                );

        Site site = siteRepository.findById(siteId)
                .orElseThrow(() ->
                        new RuntimeException("Site not found")
                );

        Optional<AttendanceLog> existing =
                attendanceLogRepository
                        .findByWorkerAndClockOutTimeIsNull(worker);

        if (existing.isPresent()) {

            throw new RuntimeException(
                    "Worker already clocked in"
            );
        }

        AttendanceLog log = new AttendanceLog();

        log.setWorker(worker);
        log.setSite(site);
        log.setClockInTime(LocalDateTime.now());

        AttendanceLog saved =
                attendanceLogRepository.save(log);

        Map<String, String> activeWorker =
                new HashMap<>();

        activeWorker.put(
                "workerId",
                worker.getId().toString()
        );

        activeWorker.put(
                "workerName",
                worker.getName()
        );

        activeWorker.put(
                "designation",
                worker.getDesignation().toString()
        );

        activeWorker.put(
                "siteId",
                site.getId().toString()
        );

        activeWorker.put(
                "siteName",
                site.getSiteName()
        );

        activeWorker.put(
                "clockInTime",
                saved.getClockInTime().toString()
        );

        try {

            redisTemplate.opsForHash().put(
                    "active_workers",
                    worker.getId().toString(),
                    activeWorker
            );

        } catch (Exception e) {

            e.printStackTrace();

            System.out.println(
                    "Redis unavailable, continuing without cache"
            );
        }

        return saved;
    }

    public AttendanceLog clockOut(
            Long workerId
    ) {

        Worker worker = workerRepository.findById(workerId)
                .orElseThrow(() ->
                        new RuntimeException("Worker not found")
                );

        AttendanceLog log =
                attendanceLogRepository
                        .findByWorkerAndClockOutTimeIsNull(worker)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Worker is not clocked in"
                                )
                        );

        LocalDateTime now = LocalDateTime.now();

        log.setClockOutTime(now);

        double totalHoursDouble =
                Duration.between(
                        log.getClockInTime(),
                        now
                ).toMinutes() / 60.0;

        BigDecimal totalHours =
                BigDecimal.valueOf(totalHoursDouble)
                        .setScale(
                                2,
                                RoundingMode.HALF_UP
                        );

        log.setTotalHours(totalHours);

        if (totalHoursDouble > 8) {

            BigDecimal overtime =
                    BigDecimal.valueOf(
                                    totalHoursDouble - 8
                            )
                            .setScale(
                                    2,
                                    RoundingMode.HALF_UP
                            );

            log.setOvertimeHours(overtime);
        }

        try {

            redisTemplate.opsForHash().delete(
                    "active_workers",
                    workerId.toString()
            );

        } catch (Exception e) {

            e.printStackTrace();

            System.out.println(
                    "Redis unavailable during delete"
            );
        }

        return attendanceLogRepository.save(log);
    }

    public Collection<Object> getActiveWorkers() {

        try {

            return redisTemplate.opsForHash()
                    .values("active_workers");

        } catch (Exception e) {

            e.printStackTrace();

            System.out.println(
                    "Redis unavailable"
            );

            return Collections.emptyList();
        }
    }

    public Page<AttendanceLog> getLogs(
            Long workerId,
            Pageable pageable
    ) {

        return attendanceLogRepository
                .findByWorkerId(
                        workerId,
                        pageable
                );
    }
}