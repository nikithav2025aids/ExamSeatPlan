package com.example.examseatplan.controller;

import com.example.examseatplan.service.ExamHallService;
import com.example.examseatplan.service.SeatingService;
import com.example.examseatplan.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {

    private final ExamHallService hallService;
    private final StudentService studentService;
    private final SeatingService seatingService;

    @Autowired
    public DashboardController(ExamHallService hallService,
                               StudentService studentService,
                               SeatingService seatingService) {
        this.hallService = hallService;
        this.studentService = studentService;
        this.seatingService = seatingService;
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("totalHalls", hallService.getHallCount());
        model.addAttribute("totalStudents", studentService.getStudentCount());
        model.addAttribute("totalCapacity", hallService.getTotalCapacity());
        model.addAttribute("allocatedStudents", seatingService.getAllocatedCount());
        model.addAttribute("activeTab", "dashboard");
        return "dashboard";
    }
}
