package com.example.dailytaskmanager.data;

import android.database.Cursor;
import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface TaskDao {

    @Insert
    long insertTask(Task task);

    @Insert
    void insertStatus(TaskStatus... statuses);

    @Update
    int updateTask(Task task);

    @Query("SELECT * FROM tasks WHERE uid = :id")
    LiveData<Task> getTask(int id);

    @Query("SELECT * FROM tasks WHERE status != 'completed' ORDER BY CASE status WHEN 'expired' THEN 1 WHEN 'in-progress' THEN 2 WHEN 'recorded' THEN 3 ELSE 4 END")
    LiveData<List<Task>> getNonCompletedTasks();

    @Query("SELECT * FROM tasks")
    List<Task> getAllTasks();

    @Query("DELETE FROM tasks WHERE task_date < :date")
    void deleteTasksBefore(String date);

    @Query("SELECT * FROM tasks WHERE status != 'completed'")
    List<Task> getNonCompletedTasksForExport();

    // --- Methods for Content Provider ---
    @Query("SELECT * FROM tasks")
    Cursor getAllTasksCursor();

    @Query("DELETE FROM tasks WHERE uid = :id")
    int deleteByUri(long id);

    @Query("SELECT uid, short_name, brief_description, difficulty, task_date, start_time, duration, status, location FROM tasks")
    Cursor getTasksCursor();

    @Query("SELECT uid, short_name, brief_description, difficulty, task_date, start_time, duration, status, location FROM tasks WHERE uid = :id")
    Cursor getTaskCursor(long id);
}
