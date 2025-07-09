package com.bootcamp.savemypodo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bootcamp.savemypodo.entity.Performance;

public interface PerformanceRepository extends JpaRepository<Performance, Long> {
}