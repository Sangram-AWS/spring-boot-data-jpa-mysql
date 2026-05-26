package com.bezkoder.spring.datajpa.repository;

import com.bezkoder.spring.datajpa.entity.Site;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SiteRepository extends JpaRepository<Site, Long> {
}