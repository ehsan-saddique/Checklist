package com.prismosis.checklist.ui.task

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

import com.prismosis.checklist.data.model.Task
import com.prismosis.checklist.data.repositories.TaskRepository
import com.prismosis.checklist.utils.Utils
import java.util.*

class TaskViewModel(private val taskRepository: TaskRepository) : ViewModel() {

    private val _taskResult = MutableLiveData<TaskResult>()
    val taskResult: LiveData<TaskResult> = _taskResult

    fun getAllTasks(): LiveData<List<Task>> {
        return taskRepository.allTasks
    }

    fun deleteTask(task: Task) {
        task.isDelete = true
        taskRepository.updateTask(task, callback = { result ->
            _taskResult.value = TaskResult(success = "Task has been deleted")
        })
    }


}