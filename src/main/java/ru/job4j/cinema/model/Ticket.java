package ru.job4j.cinema.model;

import java.time.LocalDateTime;
import java.util.Objects;

public class Ticket {

    private int id;
    private int sessId;
    private int posRow;
    private int cell;
    private int userId;
    private LocalDateTime date;
    private boolean condition;
    private double price;

    @SuppressWarnings("checkstyle:parameternumber")
    public Ticket(int id, int sessId, int posRow, int cell, int userId, LocalDateTime date, boolean condition, double price) {
        this.id = id;
        this.sessId = sessId;
        this.posRow = posRow;
        this.cell = cell;
        this.userId = userId;
        this.date = date;
        this.condition = condition;
        this.price = price;
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

    public int getPosRow() {
        return posRow;
    }

    public void setPosRow(int posRow) {
        this.posRow = posRow;
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

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Ticket ticket = (Ticket) o;
        return id == ticket.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
