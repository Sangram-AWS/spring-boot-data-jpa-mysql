package com.bezkoder.spring.datajpa.entity;

import com.bezkoder.spring.datajpa.enums.Designation;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "workers", indexes = {
        @Index(name = "idx_worker_phone", columnList = "phone"),
        @Index(name = "idx_worker_active", columnList = "active")
})
public class Worker {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "phone", nullable = false, unique = true)
    private String phone;

    @Enumerated(EnumType.STRING)
    @Column(name = "designation", nullable = false)
    private Designation designation;

    @Column(name = "daily_wage_rate", nullable = false, precision = 10, scale = 2)
    private BigDecimal dailyWageRate;

    @Column(name = "active", nullable = false)
    private boolean active = true;
}