package com.example.examseatplan.service;

import com.example.examseatplan.entity.ExamHall;
import com.example.examseatplan.entity.SeatAllocation;
import com.example.examseatplan.entity.Student;
import com.example.examseatplan.repository.ExamHallRepository;
import com.example.examseatplan.repository.SeatAllocationRepository;
import com.example.examseatplan.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class SeatingService {

    private final ExamHallRepository hallRepository;
    private final StudentRepository studentRepository;
    private final SeatAllocationRepository allocationRepository;

    @Autowired
    public SeatingService(ExamHallRepository hallRepository,
                          StudentRepository studentRepository,
                          SeatAllocationRepository allocationRepository) {
        this.hallRepository = hallRepository;
        this.studentRepository = studentRepository;
        this.allocationRepository = allocationRepository;
    }

    public List<SeatAllocation> getAllAllocations() {
        return allocationRepository.findAll();
    }

    public List<SeatAllocation> getAllocationsByHall(Long hallId) {
        return allocationRepository.findByHallIdOrderByRowNumAscColNumAsc(hallId);
    }

    public long getAllocatedCount() {
        return allocationRepository.count();
    }

    public Optional<SeatAllocation> findAllocationByRegisterNumber(String regNo) {
        if (regNo == null || regNo.trim().isEmpty()) {
            return Optional.empty();
        }
        return allocationRepository.findByStudentRegisterNumber(regNo.trim());
    }

    @Transactional
    public void clearSeating() {
        allocationRepository.deleteAllInBatch();
    }

    @Transactional
    public List<SeatAllocation> generateSeating() {
        List<ExamHall> halls = hallRepository.findAllByOrderByIdAsc();
        List<Student> students = studentRepository.findAllByOrderByIdAsc();

        if (halls.isEmpty()) {
            throw new IllegalStateException("No exam halls available for seating allocation.");
        }

        if (students.isEmpty()) {
            throw new IllegalStateException("No registered students to allocate.");
        }

        int totalRequiredSeats = students.size();
        int totalAvailableCapacity = halls.stream().mapToInt(ExamHall::getCapacity).sum();

        if (totalRequiredSeats > totalAvailableCapacity) {
            throw new IllegalStateException("Not enough seating capacity. Required: " + totalRequiredSeats +
                    ", Available: " + totalAvailableCapacity + ".");
        }

        // Group students by Class & Section for optimal separation
        Map<String, List<Student>> studentGroups = new HashMap<>();
        for (Student student : students) {
            String groupKey = student.getClassName().trim() + " - " + student.getSection().trim();
            studentGroups.computeIfAbsent(groupKey, k -> new ArrayList<>()).add(student);
        }

        // Clear existing allocations atomically
        allocationRepository.deleteAllInBatch();

        List<SeatAllocation> newAllocations = new ArrayList<>();
        int allocatedStudentCount = 0;

        for (ExamHall hall : halls) {
            if (allocatedStudentCount >= totalRequiredSeats) {
                break;
            }

            int hallRows = hall.getRows();
            int hallCols = hall.getColumns();
            int hallCapacity = hall.getCapacity();
            int seatsAssignedInHall = 0;

            // Maintain a 2D matrix of group keys placed in current hall to check neighbors
            String[][] gridGroups = new String[hallRows + 1][hallCols + 1];

            for (int r = 1; r <= hallRows; r++) {
                for (int c = 1; c <= hallCols; c++) {
                    if (seatsAssignedInHall >= hallCapacity || allocatedStudentCount >= totalRequiredSeats) {
                        break;
                    }

                    String leftGroup = (c > 1) ? gridGroups[r][c - 1] : null;
                    String topGroup = (r > 1) ? gridGroups[r - 1][c] : null;

                    String selectedGroupKey = selectBestGroup(studentGroups, leftGroup, topGroup);
                    if (selectedGroupKey == null) {
                        // Fallback if no students left
                        break;
                    }

                    List<Student> groupList = studentGroups.get(selectedGroupKey);
                    Student assignedStudent = groupList.remove(0);
                    if (groupList.isEmpty()) {
                        studentGroups.remove(selectedGroupKey);
                    }

                    gridGroups[r][c] = selectedGroupKey;

                    String seatNumber = hall.getHallName() + "-R" + r + "-C" + c;
                    SeatAllocation allocation = new SeatAllocation(
                            assignedStudent,
                            hall,
                            r,
                            c,
                            seatNumber
                    );

                    newAllocations.add(allocation);
                    seatsAssignedInHall++;
                    allocatedStudentCount++;
                }
            }
        }

        return allocationRepository.saveAll(newAllocations);
    }

    /**
     * Selects the student class/section group that maximizes separation from left and top neighbors.
     */
    private String selectBestGroup(Map<String, List<Student>> studentGroups, String leftGroup, String topGroup) {
        if (studentGroups.isEmpty()) {
            return null;
        }

        String bestKey = null;
        int maxCount = -1;

        // Priority 1: Avoid both left and top neighbors
        for (Map.Entry<String, List<Student>> entry : studentGroups.entrySet()) {
            String groupKey = entry.getKey();
            int groupSize = entry.getValue().size();

            if (!groupKey.equals(leftGroup) && !groupKey.equals(topGroup)) {
                if (groupSize > maxCount) {
                    maxCount = groupSize;
                    bestKey = groupKey;
                }
            }
        }

        if (bestKey != null) {
            return bestKey;
        }

        // Priority 2: Avoid at least left neighbor
        maxCount = -1;
        for (Map.Entry<String, List<Student>> entry : studentGroups.entrySet()) {
            String groupKey = entry.getKey();
            int groupSize = entry.getValue().size();

            if (!groupKey.equals(leftGroup)) {
                if (groupSize > maxCount) {
                    maxCount = groupSize;
                    bestKey = groupKey;
                }
            }
        }

        if (bestKey != null) {
            return bestKey;
        }

        // Priority 3: Fallback to group with highest remaining size
        for (Map.Entry<String, List<Student>> entry : studentGroups.entrySet()) {
            int groupSize = entry.getValue().size();
            if (groupSize > maxCount) {
                maxCount = groupSize;
                bestKey = entry.getKey();
            }
        }

        return bestKey;
    }
}
