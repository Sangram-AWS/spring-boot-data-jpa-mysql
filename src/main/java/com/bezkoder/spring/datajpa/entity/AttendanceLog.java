package com.bezkoder.spring.datajpa.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@NamedEntityGraph(
        name = "AttendanceLog.withWorkerAndSite",
        attributeNodes = {
                @NamedAttributeNode("worker"),
                @NamedAttributeNode("site")
        }
)
@Table(
        name = "attendance_logs",
        indexes = {
                @Index(
                        name = "idx_attendance_worker",
                        columnList = "worker_id"
                ),
                @Index(
                        name = "idx_attendance_site",
                        columnList = "site_id"
                ),
                @Index(
                        name = "idx_attendance_clockin",
                        columnList = "clock_in_time"
                )
        }
)
public class AttendanceLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "worker_id", nullable = false)
    @JsonIgnoreProperties({
            "hibernateLazyInitializer",
            "handler"
    })
    private Worker worker;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "site_id", nullable = false)
    @JsonIgnoreProperties({
            "hibernateLazyInitializer",
            "handler"
    })
    private Site site;

    @Column(name = "clock_in_time", nullable = false)
    private LocalDateTime clockInTime;

    @Column(name = "clock_out_time")
    private LocalDateTime clockOutTime;

    @Column(
            name = "total_hours",
            precision = 5,
            scale = 2
    )
    private BigDecimal totalHours;

    @Column(
            name = "overtime_hours",
            precision = 5,
            scale = 2
    )
    private BigDecimal overtimeHours;

    @Column(name = "flagged", nullable = false)
    private boolean flagged = false;
}