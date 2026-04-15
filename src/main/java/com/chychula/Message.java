package com.chychula;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

public class Message implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private String name;
    private String eddr;
    private int count;
    private LocalDateTime createdAt;

    public Message(String name, String eddr, int count, LocalDateTime createdAt) {
        this.name = name;
        this.eddr = eddr;
        this.count = count;
        this.createdAt = createdAt;
    }

    public Message() {
    }

    public String getName() {
        return name;
    }

    public String getEddr() {
        return eddr;
    }

    public int getCount() {
        return count;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEddr(String eddr) {
        this.eddr = eddr;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "Message{" +
                "name='" + name + '\'' +
                ", eddr='" + eddr + '\'' +
                ", count=" + count +
                ", createdAt=" + createdAt +
                '}';
    }
}