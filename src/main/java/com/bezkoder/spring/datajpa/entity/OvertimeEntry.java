package com.bezkoder.spring.datajpa.entity;

import com.bezkoder.spring.datajpa.enums.SettlementStatus;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "overtime_entries", indexes = {
        @Index(name = "idx_overtime_worker", columnList = "worker_id"),
        @Index(name = "idx_overtime_date", columnList = "date"),
        @Index(name = "idx_overtime_status", columnList = "settlement_status")
})
public class OvertimeEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "worker_id", nullable = false)
    private Worker worker;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "attendance_id", nullable = false)
    private AttendanceLog attendance;

    @Column(name = "date", nullable = false)
    private LocalDate date;

    @Column(name = "overtime_hours", nullable = false, precision = 5, scale = 2)
    private BigDecimal overtimeHours;

    @Column(name = "overtime_rate", nullable = false, precision = 10, scale = 2)
    private BigDecimal overtimeRate;

    @Column(name = "amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(name = "settlement_status", nullable = false)
    private SettlementStatus settlementStatus = SettlementStatus.PENDING;
}