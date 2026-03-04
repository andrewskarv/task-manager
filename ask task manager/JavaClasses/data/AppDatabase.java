package com.example.dailytaskmanager.data;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import java.util.concurrent.Executors;

// Bumped version to 2 because of the schema change (added index to Task table)
@Database(entities = {Task.class, TaskStatus.class}, version = 2, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    public abstract TaskDao taskDao();

    private static volatile AppDatabase INSTANCE;

    public static AppDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    AppDatabase.class, "task_database")
                            // Destroys and re-creates the database on a schema change.
                            .fallbackToDestructiveMigration()
                            .addCallback(new RoomDatabase.Callback() {
                                @Override
                                public void onCreate(@NonNull SupportSQLiteDatabase db) {
                                    super.onCreate(db);
                                    Executors.newSingleThreadExecutor().execute(() -> {
                                        INSTANCE.taskDao().insertStatus(
                                                new TaskStatus("recorded"),
                                                new TaskStatus("in-progress"),
                                                new TaskStatus("expired"),
                                                new TaskStatus("completed")
                                        );
                                    });
                                }
                            })
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
