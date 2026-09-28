package com.example.examseatplan.controller;

import com.example.examseatplan.entity.ExamHall;
import com.example.examseatplan.entity.SeatAllocation;
import com.example.examseatplan.entity.Student;
import com.example.examseatplan.service.ExamHallService;
import com.example.examseatplan.service.SeatingService;
import com.example.examseatplan.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.*;

@Controller
public class SeatingController {

    private final SeatingService seatingService;
    private final ExamHallService hallService;
    private final StudentService studentService;

    @Autowired
    public SeatingController(SeatingService seatingService,
                             ExamHallService hallService,
                             StudentService studentService) {
        this.seatingService = seatingService;
        this.hallService = hallService;
        this.studentService = studentService;
    }

    @GetMapping("/seating")
    public String viewSeatingChart(Model model) {
        List<ExamHall> halls = hallService.getAllHalls();
        List<SeatAllocation> allAllocations = seatingService.getAllAllocations();

        // Auto-generate seating automatically if halls & students exist but no seating plan exists yet
        if (allAllocations.isEmpty() && !halls.isEmpty() && studentService.getStudentCount() > 0) {
            try {
                allAllocations = seatingService.generateSeating();
                if (!model.containsAttribute("successMessage")) {
                    model.addAttribute("successMessage", "Seating arrangement generated automatically!");
                }
            } catch (Exception e) {
                model.addAttribute("errorMessage", e.getMessage());
            }
        }

        // Key: "hallId-rowNum-colNum" -> SeatAllocation (e.g., "1-1-1")
        Map<String, SeatAllocation> hallSeatMap = new HashMap<>();
        for (SeatAllocation alloc : allAllocations) {
            if (alloc != null && alloc.getHall() != null) {
                String key = alloc.getHall().getId() + "-" + alloc.getRowNum() + "-" + alloc.getColNum();
                hallSeatMap.put(key, alloc);
            }
        }

        model.addAttribute("halls", halls);
        model.addAttribute("hallSeatMap", hallSeatMap);
        model.addAttribute("totalAllocated", seatingService.getAllocatedCount());
        model.addAttribute("totalStudents", studentService.getStudentCount());
        model.addAttribute("totalCapacity", hallService.getTotalCapacity());
        model.addAttribute("activeTab", "seating");

        return "seating";
    }

    @PostMapping("/seating/generate")
    public String generateSeating(RedirectAttributes redirectAttributes) {
        try {
            List<SeatAllocation> result = seatingService.generateSeating();
            redirectAttributes.addFlashAttribute("successMessage",
                    "Seating arrangement generated successfully! " + result.size() + " students allocated.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/seating";
    }

    @PostMapping("/seating/clear")
    public String clearSeating(RedirectAttributes redirectAttributes) {
        try {
            seatingService.clearSeating();
            redirectAttributes.addFlashAttribute("successMessage", "Seating plan cleared successfully.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/seating";
    }

    @GetMapping("/find-seat")
    public String findSeat(@RequestParam(name = "registerNumber", required = false) String regNo, Model model) {
        model.addAttribute("activeTab", "find-seat");
        model.addAttribute("searchedRegNo", regNo);

        if (regNo != null && !regNo.trim().isEmpty()) {
            String trimmedRegNo = regNo.trim();
            Optional<Student> studentOpt = studentService.getStudentByRegisterNumber(trimmedRegNo);

            if (studentOpt.isEmpty()) {
                model.addAttribute("errorMessage", "Student not found.");
            } else {
                Student student = studentOpt.get();
                model.addAttribute("student", student);

                Optional<SeatAllocation> allocationOpt = seatingService.findAllocationByRegisterNumber(trimmedRegNo);
                if (allocationOpt.isEmpty()) {
                    model.addAttribute("infoMessage", "Seating has not been generated for this student yet.");
                } else {
                    model.addAttribute("allocation", allocationOpt.get());
                }
            }
        }

        return "find-seat";
    }
}
