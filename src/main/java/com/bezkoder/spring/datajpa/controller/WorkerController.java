package com.bezkoder.spring.datajpa.controller;

import com.bezkoder.spring.datajpa.entity.Worker;
import com.bezkoder.spring.datajpa.entity.Site;
import com.bezkoder.spring.datajpa.repository.WorkerRepository;
import com.bezkoder.spring.datajpa.repository.SiteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/workers")
@RequiredArgsConstructor
public class WorkerController {
    private final WorkerRepository workerRepository;
    private final SiteRepository siteRepository;

    @PostMapping
    public ResponseEntity<Worker> createWorker(@RequestBody Worker worker) {
        return ResponseEntity.ok(workerRepository.save(worker));
    }

    @GetMapping
    public ResponseEntity<List<Worker>> getAllWorkers() {
        return ResponseEntity.ok(workerRepository.findAll());
    }

    @PostMapping("/sites")
    public ResponseEntity<Site> createSite(@RequestBody Site site) {
        return ResponseEntity.ok(siteRepository.save(site));
    }

    @GetMapping("/sites")
    public ResponseEntity<List<Site>> getAllSites() {
        return ResponseEntity.ok(siteRepository.findAll());
    }
}