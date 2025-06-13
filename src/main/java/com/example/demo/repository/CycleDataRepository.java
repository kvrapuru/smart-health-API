package com.example.demo.repository;

import com.example.demo.model.CycleData;
import com.example.demo.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.List;

public interface CycleDataRepository extends JpaRepository<CycleData, Long> {
    List<CycleData> findByUser(User user);
    List<CycleData> findByUserAndStartDateBetween(User user, LocalDate start, LocalDate end);
} 