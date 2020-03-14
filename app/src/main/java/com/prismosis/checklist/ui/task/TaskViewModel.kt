package com.prismosis.checklist.ui.task

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.prismosis.checklist.data.model.DTOTask

import com.prismosis.checklist.data.model.Task
import com.prismosis.checklist.data.repositories.TaskRepository
import com.prismosis.checklist.utils.Enum
import com.prismosis.checklist.utils.Utils
import java.util.*

class TaskViewModel(private val taskRepository: TaskRepository) : ViewModel() {

    private val _taskResult = MutableLiveData<TaskResult>()
    val taskResult: LiveData<TaskResult> = _taskResult

    fun getAllTasks(): LiveData<List<DTOTask>> {
        return taskRepository.getAllTasks()
    }

    fun deleteTask(task: DTOTask) {
        task.isDeleted = true
        task.isDirty = true
        taskRepository.updateTask(task, callback = { result ->
            _taskResult.value = TaskResult(success = "Task has been deleted")
        })
    }

    fun changeTaskStatus(task: DTOTask, changeToStatus: Enum.TaskStatus) {
        task.isDirty = true

        taskRepository.updateTaskStatus(task, changeToStatus, callback = {
            _taskResult.value = TaskResult(success = "Task status has been changed to ${changeToStatus.string}")
        })
    }


}
