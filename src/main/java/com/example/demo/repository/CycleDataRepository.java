package com.example.demo.repository;

import com.example.demo.model.CycleData;
import com.example.demo.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface CycleDataRepository extends JpaRepository<CycleData, Long> {
    List<CycleData> findByUser(User user);
    List<CycleData> findByUserOrderByStartDateDesc(User user);
    Page<CycleData> findByUser(User user, Pageable pageable);
    List<CycleData> findByUserAndStartDateBetween(User user, LocalDate start, LocalDate end);
    Page<CycleData> findByUserAndStartDateBetween(User user, LocalDate start, LocalDate end, Pageable pageable);
} 