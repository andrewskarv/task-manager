package com.example.dailytaskmanager;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.example.dailytaskmanager.data.AppDatabase;
import com.example.dailytaskmanager.data.Task;
import com.example.dailytaskmanager.ui.AddTaskActivity;
import com.example.dailytaskmanager.ui.SettingsActivity;
import com.example.dailytaskmanager.ui.TaskAdapter;
import com.example.dailytaskmanager.ui.TaskDetailActivity;
import com.example.dailytaskmanager.ui.TaskViewModel;
import com.example.dailytaskmanager.worker.TaskUpdateWorker;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    private TaskViewModel taskViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        RecyclerView recyclerView = findViewById(R.id.rvTasks);
        LinearLayout emptyView = findViewById(R.id.emptyView);
        FloatingActionButton fab = findViewById(R.id.fabAddTask);

        fab.setOnClickListener(view -> {
            startActivity(new Intent(MainActivity.this, AddTaskActivity.class));
        });

        final TaskAdapter adapter = new TaskAdapter(new ArrayList<>(), task -> {
            Intent intent = new Intent(MainActivity.this, TaskDetailActivity.class);
            intent.putExtra("TASK_ID", task.uid);
            startActivity(intent);
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        taskViewModel = new ViewModelProvider(this).get(TaskViewModel.class);
        taskViewModel.getNonCompletedTasks().observe(this, tasks -> {
            adapter.setTasks(tasks);
            if (tasks == null || tasks.isEmpty()) {
                recyclerView.setVisibility(View.GONE);
                emptyView.setVisibility(View.VISIBLE);
            } else {
                recyclerView.setVisibility(View.VISIBLE);
                emptyView.setVisibility(View.GONE);
            }
        });

        // --- SCHEDULE PERIODIC WORK (Requirement C) ---
        PeriodicWorkRequest workRequest = new PeriodicWorkRequest.Builder(
                TaskUpdateWorker.class, 1, TimeUnit.HOURS)
                .build();
        WorkManager.getInstance(this).enqueue(workRequest);
        // -----------------------------------------------
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        } else if (itemId == R.id.action_export) {
            exportTasks();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void exportTasks() {
        Executors.newSingleThreadExecutor().execute(() -> {
            List<Task> tasks = AppDatabase.getDatabase(this).taskDao().getNonCompletedTasksForExport();
            StringBuilder sb = new StringBuilder();
            for (Task task : tasks) {
                sb.append("Task ID: ").append(task.uid).append("\n");
                sb.append("Short Name: ").append(task.shortName).append("\n");
                sb.append("Description: ").append(task.briefDescription).append("\n");
                sb.append("Difficulty: ").append(task.difficulty).append("\n");
                sb.append("Date: ").append(task.taskDate).append("\n");
                sb.append("Start Time: ").append(task.startTime).append("\n");
                sb.append("Duration: ").append(task.duration).append("\n");
                sb.append("Status: ").append(task.status).append("\n");
                sb.append("Location: ").append(task.location).append("\n\n");
            }

            final String content = sb.toString();

            try {
                ContentResolver resolver = getApplicationContext().getContentResolver();
                ContentValues contentValues = new ContentValues();
                contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, "tasks.txt");
                contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "text/plain");
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS);
                }

                Uri uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues);
                if (uri == null) throw new Exception("Failed to create MediaStore record.");

                try (OutputStream outputStream = resolver.openOutputStream(uri)) {
                    if (outputStream == null) throw new Exception("Failed to open output stream.");
                    outputStream.write(content.getBytes());
                }

                runOnUiThread(() -> Toast.makeText(this, "Tasks exported to Downloads folder", Toast.LENGTH_LONG).show());

            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(this, "Export failed!", Toast.LENGTH_SHORT).show());
            }
        });
    }
}
