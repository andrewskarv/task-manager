package com.example.dailytaskmanager.data;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "tasks",
        foreignKeys = @ForeignKey(entity = TaskStatus.class,
                                  parentColumns = "name",
                                  childColumns = "status",
                                  onDelete = ForeignKey.CASCADE),
        indices = {@Index(value = {"status"})})
public class Task {
    @PrimaryKey(autoGenerate = true)
    public int uid;

    @ColumnInfo(name = "short_name")
    public String shortName;

    @ColumnInfo(name = "brief_description")
    public String briefDescription;

    public int difficulty;

    @ColumnInfo(name = "task_date")
    public String taskDate;

    @ColumnInfo(name = "start_time")
    public String startTime;

    public int duration;

    public String status;

    public String location;
}
