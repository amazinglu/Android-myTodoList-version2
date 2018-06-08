package com.example.amazinglu.todo_list.model;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Folder {
    public String id;
    public String title;
    public List<Todo> allList;

    public Folder(String title) {
        id = UUID.randomUUID().toString();
        this.title = title;
        allList = new ArrayList<>();
    }
}
