package com.example.dailytaskmanager.data;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "statuses")
public class TaskStatus {
    @PrimaryKey
    @NonNull
    public String name;

    public TaskStatus(@NonNull String name) {
        this.name = name;
    }
}
