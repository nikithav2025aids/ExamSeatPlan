package com.example.examseatplan.service;

import com.example.examseatplan.entity.Student;
import com.example.examseatplan.repository.SeatAllocationRepository;
import com.example.examseatplan.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class StudentService {

    private final StudentRepository studentRepository;
    private final SeatAllocationRepository allocationRepository;

    @Autowired
    public StudentService(StudentRepository studentRepository, SeatAllocationRepository allocationRepository) {
        this.studentRepository = studentRepository;
        this.allocationRepository = allocationRepository;
    }

    public List<Student> getAllStudents() {
        return studentRepository.findAllByOrderByIdAsc();
    }

    public Optional<Student> getStudentById(Long id) {
        return studentRepository.findById(id);
    }

    public Optional<Student> getStudentByRegisterNumber(String regNo) {
        if (regNo == null || regNo.trim().isEmpty()) {
            return Optional.empty();
        }
        return studentRepository.findByRegisterNumber(regNo.trim());
    }

    public List<Student> searchStudents(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getAllStudents();
        }
        return studentRepository.searchStudents(keyword.trim());
    }

    @Transactional
    public Student saveStudent(Student student) {
        validateStudent(student, null);
        return studentRepository.save(student);
    }

    @Transactional
    public Student updateStudent(Long id, Student studentDetails) {
        Student existingStudent = studentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Student not found with id: " + id));

        validateStudent(studentDetails, id);

        existingStudent.setRegisterNumber(studentDetails.getRegisterNumber().trim());
        existingStudent.setStudentName(studentDetails.getStudentName().trim());
        existingStudent.setClassName(studentDetails.getClassName().trim());
        existingStudent.setSection(studentDetails.getSection().trim());

        return studentRepository.save(existingStudent);
    }

    @Transactional
    public void deleteStudent(Long id) {
        if (!studentRepository.existsById(id)) {
            throw new IllegalArgumentException("Student not found with id: " + id);
        }
        allocationRepository.deleteByStudentId(id);
        studentRepository.deleteById(id);
    }

    public long getStudentCount() {
        return studentRepository.count();
    }

    private void validateStudent(Student student, Long currentId) {
        if (student.getRegisterNumber() == null || student.getRegisterNumber().trim().isEmpty()) {
            throw new IllegalArgumentException("Register number is required.");
        }
        if (student.getStudentName() == null || student.getStudentName().trim().isEmpty()) {
            throw new IllegalArgumentException("Student name is required.");
        }
        if (student.getClassName() == null || student.getClassName().trim().isEmpty()) {
            throw new IllegalArgumentException("Class is required.");
        }
        if (student.getSection() == null || student.getSection().trim().isEmpty()) {
            throw new IllegalArgumentException("Section is required.");
        }

        String regNo = student.getRegisterNumber().trim();
        boolean exists = (currentId == null) ?
                studentRepository.existsByRegisterNumber(regNo) :
                studentRepository.existsByRegisterNumberAndIdNot(regNo, currentId);

        if (exists) {
            throw new IllegalArgumentException("Student register number already exists.");
        }
    }
}
