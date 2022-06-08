package ru.job4j.cinema.model;

import java.time.LocalDateTime;
import java.util.Objects;

public class Ticket {

    private int id;
    private int sessId;
    private int row;
    private int cell;
    private int userId;
    private LocalDateTime date;
    private boolean condition;

    public Ticket(int id, int sessId, int row, int cell, int userId, LocalDateTime date, boolean condition) {
        this.id = id;
        this.sessId = sessId;
        this.row = row;
        this.cell = cell;
        this.userId = userId;
        this.date = date;
        this.condition = condition;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getSessId() {
        return sessId;
    }

    public void setSessId(int sessId) {
        this.sessId = sessId;
    }

    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public int getCell() {
        return cell;
    }

    public void setCell(int cell) {
        this.cell = cell;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public boolean isCondition() {
        return condition;
    }

    public void setCondition(boolean condition) {
        this.condition = condition;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Ticket ticket = (Ticket) o;
        return id == ticket.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Ticket{" +
                "id=" + id +
                ", sessId=" + sessId +
                ", row=" + row +
                ", cell=" + cell +
                ", userId=" + userId +
                ", date=" + date +
                ", condition=" + condition +
                '}';
    }
}
