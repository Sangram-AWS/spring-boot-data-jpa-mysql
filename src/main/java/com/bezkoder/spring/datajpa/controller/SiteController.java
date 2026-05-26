package com.bezkoder.spring.datajpa.controller;

import com.bezkoder.spring.datajpa.entity.Site;
import com.bezkoder.spring.datajpa.repository.SiteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sites")
@RequiredArgsConstructor
public class SiteController {

    private final SiteRepository siteRepository;

    @PostMapping
    public ResponseEntity<Site> create(@RequestBody Site site) {
        return ResponseEntity.ok(siteRepository.save(site));
    }

    @GetMapping
    public ResponseEntity<List<Site>> getAll() {
        return ResponseEntity.ok(siteRepository.findAll());
    }
}