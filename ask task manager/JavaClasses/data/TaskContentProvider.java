package com.example.dailytaskmanager.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class TaskContentProvider extends ContentProvider {

    public static final String AUTHORITY = "com.example.dailytaskmanager.provider";
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/tasks");

    private static final int TASKS = 1;
    private static final int TASK_ID = 2;

    private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    static {
        uriMatcher.addURI(AUTHORITY, "tasks", TASKS);
        uriMatcher.addURI(AUTHORITY, "tasks/#", TASK_ID);
    }

    private TaskDao taskDao;

    @Override
    public boolean onCreate() {
        taskDao = AppDatabase.getDatabase(getContext()).taskDao();
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        Cursor cursor;
        int match = uriMatcher.match(uri);
        if (match == TASKS) {
            cursor = taskDao.getTasksCursor();
        } else if (match == TASK_ID) {
            cursor = taskDao.getTaskCursor(ContentUris.parseId(uri));
        } else {
            throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null; // Not required for this project
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        long id = taskDao.insertTask(fromContentValues(values));
        if (id > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
            return ContentUris.withAppendedId(CONTENT_URI, id);
        } else {
            return null;
        }
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        int count = taskDao.deleteByUri(ContentUris.parseId(uri));
        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        int count = taskDao.updateTask(fromContentValues(values));
        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    private Task fromContentValues(ContentValues values) {
        Task task = new Task();
        if (values.containsKey("uid")) task.uid = values.getAsInteger("uid");
        if (values.containsKey("short_name")) task.shortName = values.getAsString("short_name");
        if (values.containsKey("brief_description")) task.briefDescription = values.getAsString("brief_description");
        if (values.containsKey("difficulty")) task.difficulty = values.getAsInteger("difficulty");
        if (values.containsKey("task_date")) task.taskDate = values.getAsString("task_date");
        if (values.containsKey("start_time")) task.startTime = values.getAsString("start_time");
        if (values.containsKey("duration")) task.duration = values.getAsInteger("duration");
        if (values.containsKey("status")) task.status = values.getAsString("status");
        if (values.containsKey("location")) task.location = values.getAsString("location");
        return task;
    }
}
