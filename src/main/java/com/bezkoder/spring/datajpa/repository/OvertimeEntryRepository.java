package com.bezkoder.spring.datajpa.repository;

import com.bezkoder.spring.datajpa.entity.OvertimeEntry;
import com.bezkoder.spring.datajpa.enums.SettlementStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface OvertimeEntryRepository extends JpaRepository<OvertimeEntry, Long> {

    // All overtime entries for a worker in a month
    List<OvertimeEntry> findByWorkerIdAndDateBetweenOrderByDateAsc(
            Long workerId,
            LocalDate from,
            LocalDate to);

    // Total overtime hours for a worker in a month
    @Query("SELECT COALESCE(SUM(o.overtimeHours), 0) FROM OvertimeEntry o " +
            "WHERE o.worker.id = :workerId " +
            "AND o.date >= :from AND o.date <= :to")
    BigDecimal sumOvertimeHoursByWorkerAndMonth(
            Long workerId,
            LocalDate from,
            LocalDate to);

    // Check settlement status
    List<OvertimeEntry> findByWorkerIdAndDateBetweenAndSettlementStatus(
            Long workerId,
            LocalDate from,
            LocalDate to,
            SettlementStatus status);
}