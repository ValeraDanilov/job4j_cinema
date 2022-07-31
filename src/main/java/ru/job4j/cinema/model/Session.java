package ru.job4j.cinema.model;

import java.time.LocalDateTime;
import java.util.Objects;

public class Session {

    private int id;
    private String name;
    private String text;
    private LocalDateTime created;
    private String data;

    public Session(int id, String name, String text, LocalDateTime created, String data) {
        this.id = id;
        this.name = name;
        this.text = text;
        this.created = created;
        this.data = data;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public LocalDateTime getCreate() {
        return created;
    }

    public void setCreate(LocalDateTime created) {
        this.created = created;
    }

    public String getData() {
        return data;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Session session = (Session) o;
        return id == session.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Sessions{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", text='" + text + '\'' +
                ", created=" + created +
                ", data='" + data + '\'' +
                '}';
    }
}
