package com.example.taskmanagementapp.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {
    @Insert
    suspend fun insertTask(task: Task) // suspend is for denoting the function asynchronously

    @Delete
    suspend fun deleteTask(task: Task)

    @Upsert
    suspend fun upsertTask(task: Task) // upsert is for denoting both inserting and updating newer version of room database have this.

    @Query("SELECT * FROM task ORDER BY dateAdded")
    fun getTasksOrderedByDateAdded(): Flow<List<Task>>

    @Query("SELECT * FROM task ORDER BY title ASC")
    fun getTasksOrderedByTitle(): Flow<List<Task>>
}