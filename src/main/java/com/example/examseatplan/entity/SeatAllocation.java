package com.example.examseatplan.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "seat_allocations")
public class SeatAllocation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "student_id", nullable = false, unique = true)
    private Student student;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "hall_id", nullable = false)
    private ExamHall hall;

    @Column(name = "row_num", nullable = false)
    private Integer rowNum;

    @Column(name = "col_num", nullable = false)
    private Integer colNum;

    @Column(name = "seat_number", nullable = false)
    private String seatNumber;

    public SeatAllocation() {
    }

    public SeatAllocation(Student student, ExamHall hall, Integer rowNum, Integer colNum, String seatNumber) {
        this.student = student;
        this.hall = hall;
        this.rowNum = rowNum;
        this.colNum = colNum;
        this.seatNumber = seatNumber;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public ExamHall getHall() {
        return hall;
    }

    public void setHall(ExamHall hall) {
        this.hall = hall;
    }

    public Integer getRowNum() {
        return rowNum;
    }

    public void setRowNum(Integer rowNum) {
        this.rowNum = rowNum;
    }

    public Integer getColNum() {
        return colNum;
    }

    public void setColNum(Integer colNum) {
        this.colNum = colNum;
    }

    public String getSeatNumber() {
        return seatNumber;
    }

    public void setSeatNumber(String seatNumber) {
        this.seatNumber = seatNumber;
    }

    // Convenience getters for row/col naming
    public Integer getRow() {
        return rowNum;
    }

    public Integer getColumn() {
        return colNum;
    }
}