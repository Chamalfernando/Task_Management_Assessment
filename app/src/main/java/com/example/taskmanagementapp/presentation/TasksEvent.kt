package com.example.taskmanagementapp.presentation

import com.example.taskmanagementapp.data.Task

sealed interface TasksEvent {

    // These are the events that user can make.
    object SortTasks : TasksEvent

    data class DeleteTask(val task: Task) : TasksEvent

    data class saveTask(
        val title : String,
        val description : String
    ) : TasksEvent

    data class updateTask(
        val id : String,
        val title : String,
        val description : String
    ) : TasksEvent
}