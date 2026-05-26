package com.bezkoder.spring.datajpa.service;

import com.bezkoder.spring.datajpa.config.OvertimeSettledEvent;
import com.bezkoder.spring.datajpa.dto.OvertimeSummaryDto;
import com.bezkoder.spring.datajpa.entity.AttendanceLog;
import com.bezkoder.spring.datajpa.entity.OvertimeEntry;
import com.bezkoder.spring.datajpa.entity.Worker;
import com.bezkoder.spring.datajpa.enums.SettlementStatus;
import com.bezkoder.spring.datajpa.exception.BusinessException;
import com.bezkoder.spring.datajpa.exception.ConflictException;
import com.bezkoder.spring.datajpa.exception.ResourceNotFoundException;
import com.bezkoder.spring.datajpa.repository.OvertimeEntryRepository;
import com.bezkoder.spring.datajpa.repository.WorkerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OvertimeService {

    private static final BigDecimal MONTHLY_OT_CAP =
            BigDecimal.valueOf(60);

    private final OvertimeEntryRepository overtimeEntryRepository;

    private final WorkerRepository workerRepository;

    private final ApplicationEventPublisher eventPublisher;

    public void createOvertimeEntry(
            AttendanceLog attendance,
            BigDecimal overtimeHours
    ) {

        Worker worker = attendance.getWorker();

        BigDecimal existingMonthlyHours =
                overtimeEntryRepository
                        .findByWorkerIdAndDateBetweenOrderByDateAsc(
                                worker.getId(),
                                attendance.getClockInTime()
                                        .toLocalDate()
                                        .withDayOfMonth(1),
                                attendance.getClockInTime()
                                        .toLocalDate()
                                        .withDayOfMonth(
                                                attendance.getClockInTime()
                                                        .toLocalDate()
                                                        .lengthOfMonth()
                                        )
                        )
                        .stream()
                        .map(OvertimeEntry::getOvertimeHours)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal remainingCap =
                MONTHLY_OT_CAP.subtract(existingMonthlyHours);

        if (remainingCap.compareTo(BigDecimal.ZERO) <= 0) {

            overtimeHours = BigDecimal.ZERO;

        } else if (
                overtimeHours.compareTo(remainingCap) > 0
        ) {

            overtimeHours = remainingCap;
        }

        if (
                overtimeHours.compareTo(BigDecimal.ZERO) <= 0
        ) {
            return;
        }

        BigDecimal dailyWage =
                worker.getDailyWageRate();

        BigDecimal hourlyRate =
                dailyWage.divide(
                        BigDecimal.valueOf(8),
                        4,
                        RoundingMode.HALF_UP
                );

        BigDecimal amount;

        double otHours =
                overtimeHours.doubleValue();

        if (otHours <= 2.0) {

            BigDecimal rate =
                    hourlyRate.multiply(
                            BigDecimal.valueOf(1.5)
                    );

            amount = rate.multiply(overtimeHours)
                    .setScale(2, RoundingMode.HALF_UP);

        } else {

            BigDecimal first2 =
                    hourlyRate
                            .multiply(BigDecimal.valueOf(1.5))
                            .multiply(BigDecimal.valueOf(2))
                            .setScale(2, RoundingMode.HALF_UP);

            BigDecimal remaining =
                    hourlyRate
                            .multiply(BigDecimal.valueOf(2.0))
                            .multiply(BigDecimal.valueOf(otHours - 2.0))
                            .setScale(2, RoundingMode.HALF_UP);

            amount = first2.add(remaining);
        }

        BigDecimal overtimeRate =
                hourlyRate
                        .multiply(BigDecimal.valueOf(1.5))
                        .setScale(2, RoundingMode.HALF_UP);

        OvertimeEntry entry =
                new OvertimeEntry();

        entry.setWorker(worker);

        entry.setAttendance(attendance);

        entry.setDate(
                attendance.getClockInTime().toLocalDate()
        );

        entry.setOvertimeHours(overtimeHours);

        entry.setOvertimeRate(overtimeRate);

        entry.setAmount(amount);

        entry.setSettlementStatus(
                SettlementStatus.PENDING
        );

        overtimeEntryRepository.save(entry);
    }

    public OvertimeSummaryDto getOvertimeSummary(
            Long workerId,
            String month
    ) {

        Worker worker =
                workerRepository.findById(workerId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Worker not found with id: "
                                                + workerId
                                )
                        );

        YearMonth yearMonth =
                YearMonth.parse(
                        month,
                        DateTimeFormatter.ofPattern("yyyy-MM")
                );

        LocalDate from =
                yearMonth.atDay(1);

        LocalDate to =
                yearMonth.atEndOfMonth();

        List<OvertimeEntry> entries =
                overtimeEntryRepository
                        .findByWorkerIdAndDateBetweenOrderByDateAsc(
                                workerId,
                                from,
                                to
                        );

        BigDecimal totalHours =
                entries.stream()
                        .map(OvertimeEntry::getOvertimeHours)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalAmount =
                entries.stream()
                        .map(OvertimeEntry::getAmount)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);

        String status =
                entries.stream()
                        .allMatch(e ->
                                e.getSettlementStatus()
                                        == SettlementStatus.SETTLED
                        )
                        ? "SETTLED"
                        : "PENDING";

        List<OvertimeSummaryDto.OvertimeDayDetail>
                breakdown =
                entries.stream()
                        .map(e ->
                                new OvertimeSummaryDto
                                        .OvertimeDayDetail(
                                        e.getDate(),
                                        e.getOvertimeHours(),
                                        e.getAmount(),
                                        e.getSettlementStatus()
                                                .name()
                                )
                        )
                        .collect(Collectors.toList());

        return new OvertimeSummaryDto(
                workerId,
                worker.getName(),
                month,
                totalHours,
                totalAmount,
                status,
                breakdown
        );
    }

    @Transactional
    public BigDecimal settleOvertime(
            Long workerId,
            String month
    ) {

        Worker worker =
                workerRepository.findById(workerId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Worker not found with id: "
                                                + workerId
                                )
                        );

        YearMonth yearMonth =
                YearMonth.parse(
                        month,
                        DateTimeFormatter.ofPattern("yyyy-MM")
                );

        if (yearMonth.equals(YearMonth.now())) {

            throw new BusinessException(
                    "Cannot settle current month. Only past months can be settled."
            );
        }

        LocalDate from =
                yearMonth.atDay(1);

        LocalDate to =
                yearMonth.atEndOfMonth();

        List<OvertimeEntry> pendingEntries =
                overtimeEntryRepository
                        .findByWorkerIdAndDateBetweenAndSettlementStatus(
                                workerId,
                                from,
                                to,
                                SettlementStatus.PENDING
                        );

        if (pendingEntries.isEmpty()) {

            throw new ConflictException(
                    "No pending overtime entries found for this worker and month."
            );
        }

        BigDecimal totalAmount =
                pendingEntries.stream()
                        .map(OvertimeEntry::getAmount)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);

        pendingEntries.forEach(entry ->
                entry.setSettlementStatus(
                        SettlementStatus.SETTLED
                )
        );

        overtimeEntryRepository.saveAll(
                pendingEntries
        );

        eventPublisher.publishEvent(
                new OvertimeSettledEvent(
                        this,
                        workerId,
                        month,
                        totalAmount
                )
        );

        return totalAmount;
    }
}