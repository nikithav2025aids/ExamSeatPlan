package com.example.examseatplan.controller;

import com.example.examseatplan.entity.ExamHall;
import com.example.examseatplan.service.ExamHallService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/halls")
public class ExamHallController {

    private final ExamHallService hallService;

    @Autowired
    public ExamHallController(ExamHallService hallService) {
        this.hallService = hallService;
    }

    @GetMapping
    public String listHalls(Model model) {
        model.addAttribute("halls", hallService.getAllHalls());
        if (!model.containsAttribute("hall")) {
            model.addAttribute("hall", new ExamHall());
        }
        model.addAttribute("activeTab", "halls");
        return "halls";
    }

    @PostMapping("/add")
    public String addHall(@ModelAttribute("hall") ExamHall hall, RedirectAttributes redirectAttributes) {
        try {
            hallService.saveHall(hall);
            redirectAttributes.addFlashAttribute("successMessage", "Exam Hall added successfully!");
            return "redirect:/halls";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            redirectAttributes.addFlashAttribute("hall", hall);
            return "redirect:/halls";
        }
    }

    @PostMapping("/edit/{id}")
    public String updateHall(@PathVariable("id") Long id, @ModelAttribute("hall") ExamHall hall, RedirectAttributes redirectAttributes) {
        try {
            hallService.updateHall(id, hall);
            redirectAttributes.addFlashAttribute("successMessage", "Exam Hall updated successfully!");
            return "redirect:/halls";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/halls";
        }
    }

    @PostMapping("/delete/{id}")
    public String deleteHall(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            hallService.deleteHall(id);
            redirectAttributes.addFlashAttribute("successMessage", "Exam Hall deleted successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/halls";
    }
}
