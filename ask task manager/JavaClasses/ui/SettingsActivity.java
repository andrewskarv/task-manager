package com.example.dailytaskmanager.ui;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.dailytaskmanager.R;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        EditText etDefaultDuration = findViewById(R.id.etDefaultDuration);
        EditText etDefaultDifficulty = findViewById(R.id.etDefaultDifficulty);
        Button btnSaveSettings = findViewById(R.id.btnSaveSettings);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        etDefaultDuration.setText(String.valueOf(prefs.getInt("default_duration", 1)));
        etDefaultDifficulty.setText(String.valueOf(prefs.getInt("default_difficulty", 5)));

        btnSaveSettings.setOnClickListener(v -> {
            try {
                int defaultDuration = Integer.parseInt(etDefaultDuration.getText().toString());
                int defaultDifficulty = Integer.parseInt(etDefaultDifficulty.getText().toString());

                if (defaultDuration <= 0) {
                    Toast.makeText(this, "Invalid Duration", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (defaultDifficulty < 0 || defaultDifficulty > 10) {
                    Toast.makeText(this, "Invalid Difficulty", Toast.LENGTH_SHORT).show();
                    return;
                }

                SharedPreferences.Editor editor = prefs.edit();
                editor.putInt("default_duration", defaultDuration);
                editor.putInt("default_difficulty", defaultDifficulty);
                editor.apply();

                Toast.makeText(this, "Settings saved!", Toast.LENGTH_SHORT).show();
                finish();
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Invalid input", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
