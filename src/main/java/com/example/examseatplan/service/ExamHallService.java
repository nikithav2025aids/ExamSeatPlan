package com.example.examseatplan.service;

import com.example.examseatplan.entity.ExamHall;
import com.example.examseatplan.repository.ExamHallRepository;
import com.example.examseatplan.repository.SeatAllocationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ExamHallService {

    private final ExamHallRepository hallRepository;
    private final SeatAllocationRepository allocationRepository;

    @Autowired
    public ExamHallService(ExamHallRepository hallRepository, SeatAllocationRepository allocationRepository) {
        this.hallRepository = hallRepository;
        this.allocationRepository = allocationRepository;
    }

    public List<ExamHall> getAllHalls() {
        return hallRepository.findAllByOrderByIdAsc();
    }

    public Optional<ExamHall> getHallById(Long id) {
        return hallRepository.findById(id);
    }

    @Transactional
    public ExamHall saveHall(ExamHall hall) {
        validateHall(hall, null);
        return hallRepository.save(hall);
    }

    @Transactional
    public ExamHall updateHall(Long id, ExamHall hallDetails) {
        ExamHall existingHall = hallRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Exam hall not found with id: " + id));

        validateHall(hallDetails, id);

        existingHall.setHallName(hallDetails.getHallName().trim());
        existingHall.setRowsCount(hallDetails.getRows());
        existingHall.setColumnsCount(hallDetails.getColumns());
        existingHall.setCapacity(hallDetails.getCapacity());

        return hallRepository.save(existingHall);
    }

    @Transactional
    public void deleteHall(Long id) {
        if (!hallRepository.existsById(id)) {
            throw new IllegalArgumentException("Exam hall not found with id: " + id);
        }
        allocationRepository.deleteByHallId(id);
        hallRepository.deleteById(id);
    }

    public int getTotalCapacity() {
        return hallRepository.findAll().stream()
                .mapToInt(ExamHall::getCapacity)
                .sum();
    }

    public long getHallCount() {
        return hallRepository.count();
    }

    private void validateHall(ExamHall hall, Long currentId) {
        if (hall.getHallName() == null || hall.getHallName().trim().isEmpty()) {
            throw new IllegalArgumentException("Hall name is required.");
        }
        if (hall.getRows() == null || hall.getRows() <= 0) {
            throw new IllegalArgumentException("Rows must be greater than 0.");
        }
        if (hall.getColumns() == null || hall.getColumns() <= 0) {
            throw new IllegalArgumentException("Columns must be greater than 0.");
        }
        if (hall.getCapacity() == null || hall.getCapacity() <= 0) {
            throw new IllegalArgumentException("Capacity must be greater than 0.");
        }

        int maxPossibleCapacity = hall.getRows() * hall.getColumns();
        if (hall.getCapacity() > maxPossibleCapacity) {
            throw new IllegalArgumentException("Hall capacity cannot exceed rows × columns.");
        }

        String trimmedName = hall.getHallName().trim();
        boolean exists = (currentId == null) ?
                hallRepository.existsByHallName(trimmedName) :
                hallRepository.existsByHallNameAndIdNot(trimmedName, currentId);

        if (exists) {
            throw new IllegalArgumentException("Hall name already exists.");
        }
    }
}

