package com.example.dailytaskmanager.worker;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.dailytaskmanager.data.AppDatabase;
import com.example.dailytaskmanager.data.Task;
import com.example.dailytaskmanager.data.TaskDao;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class TaskUpdateWorker extends Worker {

    public TaskUpdateWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        try {
            TaskDao taskDao = AppDatabase.getDatabase(getApplicationContext()).taskDao();
            List<Task> tasks = taskDao.getAllTasks();
            Calendar now = Calendar.getInstance();

            // --- 1. DELETE OLD TASKS (Requirement C) ---
            Calendar yesterday = Calendar.getInstance();
            yesterday.add(Calendar.DATE, -1);
            String yesterdayStr = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(yesterday.getTime());
            taskDao.deleteTasksBefore(yesterdayStr);
            // ---------------------------------------------

            for (Task task : tasks) {
                if (task.status.equals("completed")) {
                    continue;
                }

                Calendar startTime = Calendar.getInstance();
                String[] timeParts = task.startTime.split(":");
                startTime.set(Calendar.HOUR_OF_DAY, Integer.parseInt(timeParts[0]));
                startTime.set(Calendar.MINUTE, Integer.parseInt(timeParts[1]));

                Calendar endTime = (Calendar) startTime.clone();
                endTime.add(Calendar.HOUR, task.duration);

                if (now.after(endTime) && !task.status.equals("expired")) {
                    task.status = "expired";
                    taskDao.updateTask(task);
                } else if (now.after(startTime) && now.before(endTime) && !task.status.equals("in-progress")) {
                    task.status = "in-progress";
                    taskDao.updateTask(task);
                }
            }

            return Result.success();
        } catch (Exception e) {
            Log.e("TaskUpdateWorker", "Error in doWork", e);
            return Result.failure();
        }
    }
}
