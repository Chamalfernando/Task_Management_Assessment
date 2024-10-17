package com.example.taskmanagementapp.presentation

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.taskmanagementapp.data.Task
import com.example.taskmanagementapp.data.TaskDao
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// implements the ViewModel
class TasksViewModel(
    private val dao: TaskDao
) : ViewModel() {

    //  Appropriate operations would be here.

    private val isSortedByDateAdded = MutableStateFlow(true)

    private val tasks =
        isSortedByDateAdded.flatMapLatest { sort ->
            if (sort) {
                dao.getTasksOrderedByDateAdded()
            } else {
                dao.getTasksOrderedByTitle()
            }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    val _state = MutableStateFlow(TaskState())

    val state =
        combine(_state, isSortedByDateAdded, tasks) { state, isSortedByDateAdded, tasks ->
            state.copy(
                tasks = tasks
            )
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), TaskState())

    fun onEvent(event: TasksEvent) {
        when (event) {

            is TasksEvent.DeleteTask -> {
                viewModelScope.launch {
                    dao.deleteTask(event.task) // since this is a suspend function we have to surround this with ViewModelScope Coroutine.
                }
            }

            is TasksEvent.saveTask -> {
                val task = Task(
                    title = state.value.title.value,
                    description = state.value.description.value,
                    dateAdded = System.currentTimeMillis()
                )

                viewModelScope.launch {
                    dao.upsertTask(task)
                }

                _state.update {
                    it.copy(
                        title = mutableStateOf(""),
                        description = mutableStateOf("")
                    )
                }
            }

            is TasksEvent.updateTask -> {
                viewModelScope.launch {
                    val updatedTask = Task(
                        id = event.id,
                        title = event.title,
                        description = event.description,
                        dateAdded = System.currentTimeMillis()
                    )
                    dao.upsertTask(updatedTask)
                }
            }

            TasksEvent.SortTasks -> {
                isSortedByDateAdded.value = !isSortedByDateAdded.value
            }
        }
    }
}