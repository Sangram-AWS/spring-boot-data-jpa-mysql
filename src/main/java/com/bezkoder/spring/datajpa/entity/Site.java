package com.bezkoder.spring.datajpa.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "sites", indexes = {
        @Index(name = "idx_site_active", columnList = "active")
})
public class Site {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "site_name", nullable = false)
    private String siteName;

    @Column(name = "location", nullable = false)
    private String location;

    @Column(name = "active", nullable = false)
    private boolean active = true;
}