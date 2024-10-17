package com.example.taskmanagementapp.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [Task::class], // this is to indicate what is the entity.
    version = 1, // this is version of the database
)
abstract class TasksDatabase : RoomDatabase() { // database impelements RoomDatabase (inheritance)
    abstract val dao: TaskDao
}