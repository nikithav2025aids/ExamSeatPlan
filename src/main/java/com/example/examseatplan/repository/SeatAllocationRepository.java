package com.example.examseatplan.repository;

import com.example.examseatplan.entity.SeatAllocation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SeatAllocationRepository extends JpaRepository<SeatAllocation, Long> {

    Optional<SeatAllocation> findByStudentRegisterNumber(String registerNumber);

    Optional<SeatAllocation> findByStudentId(Long studentId);

    List<SeatAllocation> findByHallId(Long hallId);

    List<SeatAllocation> findByHallIdOrderByRowNumAscColNumAsc(Long hallId);

    void deleteByHallId(Long hallId);

    void deleteByStudentId(Long studentId);
}

