package com.example.examseatplan.controller;

import com.example.examseatplan.entity.ExamHall;
import com.example.examseatplan.entity.SeatAllocation;
import com.example.examseatplan.entity.Student;
import com.example.examseatplan.service.ExamHallService;
import com.example.examseatplan.service.SeatingService;
import com.example.examseatplan.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class ApiController {

    private final ExamHallService hallService;
    private final StudentService studentService;
    private final SeatingService seatingService;

    @Autowired
    public ApiController(ExamHallService hallService,
                         StudentService studentService,
                         SeatingService seatingService) {
        this.hallService = hallService;
        this.studentService = studentService;
        this.seatingService = seatingService;
    }

    // --- Exam Halls REST API ---

    @GetMapping("/halls")
    public ResponseEntity<List<ExamHall>> getAllHalls() {
        return ResponseEntity.ok(hallService.getAllHalls());
    }

    @GetMapping("/halls/{id}")
    public ResponseEntity<?> getHallById(@PathVariable Long id) {
        return hallService.getHallById(id)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(createErrorMap("Hall not found with id: " + id)));
    }

    @PostMapping("/halls")
    public ResponseEntity<?> createHall(@RequestBody ExamHall hall) {
        try {
            ExamHall created = hallService.saveHall(hall);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(createErrorMap(e.getMessage()));
        }
    }

    @PutMapping("/halls/{id}")
    public ResponseEntity<?> updateHall(@PathVariable Long id, @RequestBody ExamHall hall) {
        try {
            ExamHall updated = hallService.updateHall(id, hall);
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(createErrorMap(e.getMessage()));
        }
    }

    @DeleteMapping("/halls/{id}")
    public ResponseEntity<?> deleteHall(@PathVariable Long id) {
        try {
            hallService.deleteHall(id);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Exam Hall deleted successfully");
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(createErrorMap(e.getMessage()));
        }
    }

    // --- Students REST API ---

    @GetMapping("/students")
    public ResponseEntity<List<Student>> getAllStudents(@RequestParam(required = false) String search) {
        if (search != null && !search.trim().isEmpty()) {
            return ResponseEntity.ok(studentService.searchStudents(search));
        }
        return ResponseEntity.ok(studentService.getAllStudents());
    }

    @GetMapping("/students/{id}")
    public ResponseEntity<?> getStudentById(@PathVariable Long id) {
        return studentService.getStudentById(id)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(createErrorMap("Student not found with id: " + id)));
    }

    @PostMapping("/students")
    public ResponseEntity<?> createStudent(@RequestBody Student student) {
        try {
            Student created = studentService.saveStudent(student);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(createErrorMap(e.getMessage()));
        }
    }

    @PutMapping("/students/{id}")
    public ResponseEntity<?> updateStudent(@PathVariable Long id, @RequestBody Student student) {
        try {
            Student updated = studentService.updateStudent(id, student);
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(createErrorMap(e.getMessage()));
        }
    }

    @DeleteMapping("/students/{id}")
    public ResponseEntity<?> deleteStudent(@PathVariable Long id) {
        try {
            studentService.deleteStudent(id);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Student deleted successfully");
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(createErrorMap(e.getMessage()));
        }
    }

    // --- Seating REST API ---

    @GetMapping("/seating")
    public ResponseEntity<List<SeatAllocation>> getAllSeating() {
        return ResponseEntity.ok(seatingService.getAllAllocations());
    }

    @PostMapping("/seating/generate")
    public ResponseEntity<?> generateSeating() {
        try {
            List<SeatAllocation> result = seatingService.generateSeating();
            return ResponseEntity.ok(result);
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(createErrorMap(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(createErrorMap(e.getMessage()));
        }
    }

    @GetMapping("/seating/student/{registerNumber}")
    public ResponseEntity<?> getStudentSeat(@PathVariable String registerNumber) {
        Optional<Student> studentOpt = studentService.getStudentByRegisterNumber(registerNumber);
        if (studentOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(createErrorMap("Student not found."));
        }

        Optional<SeatAllocation> allocOpt = seatingService.findAllocationByRegisterNumber(registerNumber);
        if (allocOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(createErrorMap("Seating has not been generated for this student yet."));
        }

        return ResponseEntity.ok(allocOpt.get());
    }

    private Map<String, String> createErrorMap(String message) {
        Map<String, String> error = new HashMap<>();
        error.put("error", message);
        return error;
    }
}
