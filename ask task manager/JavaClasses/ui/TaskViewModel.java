package com.example.dailytaskmanager.ui;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.dailytaskmanager.data.AppDatabase;
import com.example.dailytaskmanager.data.Task;
import com.example.dailytaskmanager.data.TaskDao;

import java.util.List;
import java.util.concurrent.Executors;

public class TaskViewModel extends AndroidViewModel {

    private final TaskDao taskDao;
    private final LiveData<List<Task>> nonCompletedTasks;

    public TaskViewModel(@NonNull Application application) {
        super(application);
        taskDao = AppDatabase.getDatabase(application).taskDao();
        nonCompletedTasks = taskDao.getNonCompletedTasks();
    }

    public LiveData<List<Task>> getNonCompletedTasks() {
        return nonCompletedTasks;
    }

    public LiveData<Task> getTask(int id) {
        return taskDao.getTask(id);
    }

    public void insert(Task task) {
        Executors.newSingleThreadExecutor().execute(() -> taskDao.insertTask(task));
    }
}
