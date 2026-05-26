package com.bezkoder.spring.datajpa.repository;

import com.bezkoder.spring.datajpa.entity.Worker;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WorkerRepository extends JpaRepository<Worker, Long> {
    Optional<Worker> findByPhone(String phone);
    boolean existsByPhone(String phone);
}
