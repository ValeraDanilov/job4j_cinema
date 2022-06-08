package ru.job4j.cinema.model;

import java.time.LocalDateTime;
import java.util.Objects;

public class Sessions {

    private int id;
    private String name;
    private String text;
    private LocalDateTime create;
    private String data;

    public Sessions(int id, String name, String text, LocalDateTime create, String data) {
        this.id = id;
        this.name = name;
        this.text = text;
        this.create = create;
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
        return create;
    }

    public void setCreate(LocalDateTime create) {
        this.create = create;
    }

    public String getData() {
        return data;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Sessions session = (Sessions) o;
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
                ", create=" + create +
                ", data='" + data + '\'' +
                '}';
    }
}
