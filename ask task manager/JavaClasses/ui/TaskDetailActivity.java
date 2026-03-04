package com.example.dailytaskmanager.ui;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.dailytaskmanager.R;
import com.example.dailytaskmanager.data.AppDatabase;
import com.example.dailytaskmanager.data.Task;

import java.util.concurrent.Executors;

public class TaskDetailActivity extends AppCompatActivity {

    private TaskViewModel taskViewModel;
    private int taskId;
    private Task currentTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_detail);

        taskViewModel = new ViewModelProvider(this).get(TaskViewModel.class);
        taskId = getIntent().getIntExtra("TASK_ID", -1);

        TextView tvShortName = findViewById(R.id.tvShortName);
        TextView tvStatus = findViewById(R.id.tvStatus);
        TextView tvDescription = findViewById(R.id.tvDescription);
        TextView tvDifficulty = findViewById(R.id.tvDifficulty);
        TextView tvDate = findViewById(R.id.tvDate);
        TextView tvStartTime = findViewById(R.id.tvStartTime);
        TextView tvDuration = findViewById(R.id.tvDuration);
        TextView tvLocation = findViewById(R.id.tvLocation);
        Button btnCompleteTask = findViewById(R.id.btnCompleteTask);
        Button btnViewLocation = findViewById(R.id.btnViewLocation);

        taskViewModel.getTask(taskId).observe(this, task -> {
            if (task != null) {
                currentTask = task;
                tvShortName.setText(task.shortName);
                tvStatus.setText("Status: " + task.status);
                tvDescription.setText("Description: " + task.briefDescription);
                tvDifficulty.setText("Difficulty: " + task.difficulty);
                tvDate.setText("Date: " + task.taskDate);
                tvStartTime.setText("Start Time: " + task.startTime);
                tvDuration.setText("Duration: " + task.duration + " hours");
                tvLocation.setText("Location: " + task.location);

                if (task.location == null || task.location.isEmpty()) {
                    btnViewLocation.setVisibility(View.GONE);
                } else {
                    btnViewLocation.setVisibility(View.VISIBLE);
                }

                if (task.status.equals("completed")) {
                    btnCompleteTask.setVisibility(View.GONE);
                }
            }
        });

        btnCompleteTask.setOnClickListener(v -> {
            if (currentTask != null) {
                currentTask.status = "completed";
                Executors.newSingleThreadExecutor().execute(() -> {
                    AppDatabase.getDatabase(getApplicationContext()).taskDao().updateTask(currentTask);
                    runOnUiThread(() -> {
                        Toast.makeText(this, "Task marked as completed!", Toast.LENGTH_SHORT).show();
                        finish();
                    });
                });
            }
        });

        btnViewLocation.setOnClickListener(v -> {
            if (currentTask != null && currentTask.location != null && !currentTask.location.isEmpty()) {
                Uri gmmIntentUri = Uri.parse("geo:0,0?q=" + Uri.encode(currentTask.location));
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);

                try {
                    startActivity(mapIntent);
                } catch (ActivityNotFoundException e) {
                    Toast.makeText(this, "No application can handle this request. Please install a maps application.", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
