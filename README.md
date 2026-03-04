# Daily Task Manager


Author: Ανδρέας Σκαρβέλης
Student ID: It2021157


Daily Task Manager is a fully functional Android application designed to help users efficiently manage their daily tasks. The app implements modern Android development practices and follows a clean architecture approach.


## It utilizes:

Room for local database storage

WorkManager for background task scheduling

ViewModel for lifecycle-aware UI logic

SharedPreferences for user settings persistence


Features Overview

### A. Task Creation

Users can create new tasks that are stored locally using Room Database.

How it works:

Tap the "+" button

Enter a task title (e.g., "Dentist Appointment")

Tap "Save"

The task immediately appears in the list

✔ Tasks are persisted locally and survive app restarts.

### B. Default Settings

The app provides a settings screen where users can define:

Default task duration

Default task difficulty

These preferences are stored using SharedPreferences.

Example:
If you set the default difficulty to 8, every new task created will automatically have difficulty level 8.


### C. Periodic Background Updates

Using WorkManager, the app automatically:

Updates task status (e.g., In Progress, Expired)

Deletes outdated tasks every hour

Example:

A task scheduled for 3:00 PM automatically becomes In Progress at 3:00 PM.

It is deleted the following day without user interaction.


### D. Task Display

The main screen:

Displays all incomplete tasks

Uses a RecyclerView

Sorts tasks by priority

Shows expired tasks at the top

Tapping a task opens its detailed view.


### E. Task Actions

From the task detail screen, users can:

Mark a task as Completed

View the task’s location in Google Maps

Example:

Tap "Mark as Completed" to finish the task.

Tap "View Location" to open the address in Maps.


### F. Task Export

Users can export all incomplete tasks to a text file.

File name: tasks.txt

Location: Downloads folder

How to use:

Open the menu

Select "Export Tasks"

The file is created automatically in Downloads


### G. Content Provider Support

The application exposes its data securely via a ContentProvider.

This allows other applications to access task data safely.

Example Use Case:

A home screen widget could display today’s tasks by retrieving data directly from the app.

Architecture & Technologies

MVVM Architecture

Room Database

WorkManager

RecyclerView

SharedPreferences

ContentProvider


## Requirements

Android Studio

Minimum SDK: (Add your min SDK here)

Target SDK: (Add your target SDK here)


## Summary

Daily Task Manager is a complete Android task management solution that demonstrates:

Persistent local storage

Background processing

Clean architecture principles

Inter-app data sharing

It fulfills all project requirements using modern Android development best practices.
