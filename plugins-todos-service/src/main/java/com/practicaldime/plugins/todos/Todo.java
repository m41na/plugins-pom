package com.practicaldime.plugins.todos;

import java.util.Date;

public class Todo {

    private Long id = Double.valueOf(Math.floor(Math.random() * 1000)).longValue();
    private String name = new Date().toString();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String toString() {
        return String.format("Task : {id: %d, name: %s}%n", id, name);
    }
}
