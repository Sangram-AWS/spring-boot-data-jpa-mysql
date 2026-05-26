package com.bezkoder.spring.datajpa.repository;

import com.bezkoder.spring.datajpa.entity.AttendanceLog;
import com.bezkoder.spring.datajpa.entity.Worker;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface AttendanceLogRepository
        extends JpaRepository<AttendanceLog, Long> {

    Optional<AttendanceLog> findByWorkerAndClockOutTimeIsNull(
            Worker worker
    );

    @EntityGraph(attributePaths = {"worker", "site"})
    Page<AttendanceLog> findByWorkerId(
            Long workerId,
            Pageable pageable
    );

    List<AttendanceLog> findByWorkerIdAndClockInTimeBetween(
            Long workerId,
            LocalDateTime from,
            LocalDateTime to
    );
}