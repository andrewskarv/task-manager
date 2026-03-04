package com.example.dailytaskmanager.ui;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.dailytaskmanager.R;
import com.example.dailytaskmanager.data.Task;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class AddTaskActivity extends AppCompatActivity {

    private TaskViewModel taskViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);

        taskViewModel = new ViewModelProvider(this).get(TaskViewModel.class);

        EditText etShortName = findViewById(R.id.etShortName);
        EditText etBriefDescription = findViewById(R.id.etBriefDescription);
        EditText etDifficulty = findViewById(R.id.etDifficulty);
        TimePicker tpStartTime = findViewById(R.id.tpStartTime);
        EditText etDuration = findViewById(R.id.etDuration);
        EditText etLocation = findViewById(R.id.etLocation);
        Button btnSaveTask = findViewById(R.id.btnSaveTask);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        etDuration.setText(String.valueOf(prefs.getInt("default_duration", 1)));
        etDifficulty.setText(String.valueOf(prefs.getInt("default_difficulty", 5)));

        btnSaveTask.setOnClickListener(v -> {
            String shortName = etShortName.getText().toString();
            if (shortName.isEmpty() || shortName.length() > 20) {
                Toast.makeText(this, "Invalid Short Name", Toast.LENGTH_SHORT).show();
                return;
            }

            String briefDescription = etBriefDescription.getText().toString();
            if (briefDescription.length() > 150) {
                Toast.makeText(this, "Description too long", Toast.LENGTH_SHORT).show();
                return;
            }

            int difficulty;
            try {
                difficulty = Integer.parseInt(etDifficulty.getText().toString());
                if (difficulty < 0 || difficulty > 10) {
                    Toast.makeText(this, "Invalid Difficulty", Toast.LENGTH_SHORT).show();
                    return;
                }
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Invalid Difficulty", Toast.LENGTH_SHORT).show();
                return;
            }

            int duration;
            try {
                duration = Integer.parseInt(etDuration.getText().toString());
                if (duration <= 0) {
                    Toast.makeText(this, "Invalid Duration", Toast.LENGTH_SHORT).show();
                    return;
                }
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Invalid Duration", Toast.LENGTH_SHORT).show();
                return;
            }

            int hour = tpStartTime.getHour();
            int minute = tpStartTime.getMinute();

            Task task = new Task();
            task.shortName = shortName;
            task.briefDescription = briefDescription;
            task.difficulty = difficulty;
            task.taskDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
            task.startTime = String.format(Locale.getDefault(), "%02d:%02d", hour, minute);
            task.duration = duration;
            task.location = etLocation.getText().toString();

            // --- Determine the correct initial status ---
            Calendar now = Calendar.getInstance();
            Calendar taskStartCalendar = Calendar.getInstance();
            taskStartCalendar.set(Calendar.HOUR_OF_DAY, hour);
            taskStartCalendar.set(Calendar.MINUTE, minute);
            taskStartCalendar.set(Calendar.SECOND, 0);

            Calendar taskEndCalendar = (Calendar) taskStartCalendar.clone();
            taskEndCalendar.add(Calendar.HOUR_OF_DAY, duration);

            if (now.after(taskEndCalendar)) {
                task.status = "expired";
            } else if (now.after(taskStartCalendar)) {
                task.status = "in-progress";
            } else {
                task.status = "recorded";
            }
            // -----------------------------------------

            taskViewModel.insert(task);
            Toast.makeText(this, "Task saved!", Toast.LENGTH_SHORT).show();
            finish();
        });
    }
}
