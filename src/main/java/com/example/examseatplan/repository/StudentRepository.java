package com.example.examseatplan.repository;

import com.example.examseatplan.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {

    boolean existsByRegisterNumber(String registerNumber);

    boolean existsByRegisterNumberAndIdNot(String registerNumber, Long id);

    Optional<Student> findByRegisterNumber(String registerNumber);

    @Query("SELECT s FROM Student s WHERE " +
            "LOWER(s.registerNumber) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(s.studentName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(s.className) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(s.section) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Student> searchStudents(@Param("keyword") String keyword);

    List<Student> findAllByOrderByIdAsc();
}
