package com.example.examseatplan.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "exam_halls")
public class ExamHall {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "hall_name", nullable = false, unique = true)
    private String hallName;

    @Column(name = "rows_count", nullable = false)
    private Integer rowsCount;

    @Column(name = "columns_count", nullable = false)
    private Integer columnsCount;

    @Column(name = "capacity", nullable = false)
    private Integer capacity;

    public ExamHall() {
    }

    public ExamHall(String hallName, Integer rowsCount, Integer columnsCount, Integer capacity) {
        this.hallName = hallName;
        this.rowsCount = rowsCount;
        this.columnsCount = columnsCount;
        this.capacity = capacity;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getHallName() {
        return hallName;
    }

    public void setHallName(String hallName) {
        this.hallName = hallName;
    }

    public Integer getRowsCount() {
        return rowsCount;
    }

    public void setRowsCount(Integer rowsCount) {
        this.rowsCount = rowsCount;
    }

    public Integer getColumnsCount() {
        return columnsCount;
    }

    public void setColumnsCount(Integer columnsCount) {
        this.columnsCount = columnsCount;
    }

    public Integer getCapacity() {
        return capacity;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }

    // Convenience getters and setters for 'rows' and 'columns' property naming
    public Integer getRows() {
        return rowsCount;
    }

    public void setRows(Integer rows) {
        this.rowsCount = rows;
    }

    public Integer getColumns() {
        return columnsCount;
    }

    public void setColumns(Integer columns) {
        this.columnsCount = columns;
    }
}

