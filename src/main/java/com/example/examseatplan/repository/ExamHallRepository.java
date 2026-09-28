package com.example.examseatplan.repository;

import com.example.examseatplan.entity.ExamHall;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ExamHallRepository extends JpaRepository<ExamHall, Long> {

    boolean existsByHallName(String hallName);

    boolean existsByHallNameAndIdNot(String hallName, Long id);

    Optional<ExamHall> findByHallName(String hallName);

    List<ExamHall> findAllByOrderByIdAsc();
}