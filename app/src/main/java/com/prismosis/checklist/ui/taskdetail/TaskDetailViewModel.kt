package com.prismosis.checklist.ui.taskdetail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.prismosis.checklist.data.model.DTOTask

import com.prismosis.checklist.data.model.Task
import com.prismosis.checklist.data.repositories.TaskRepository
import com.prismosis.checklist.ui.task.TaskResult
import com.prismosis.checklist.utils.Enum

class TaskDetailViewModel(private val taskRepository: TaskRepository) : ViewModel() {

    private val _taskDetailResult = MutableLiveData<TaskResult>()
    val taskResult: LiveData<TaskResult> = _taskDetailResult

    fun getAllSubTasks(taskId: String): LiveData<List<DTOTask>> {
        return taskRepository.getAllSubTasks(taskId)
    }

    fun deleteTask(task: DTOTask) {
        task.isDeleted = true
        task.isDirty = true
        taskRepository.updateTask(task, callback = { result ->
            _taskDetailResult.value = TaskResult(success = "Sub task has been deleted")
        })
    }

    fun changeTaskStatus(task: DTOTask, changeToStatus: Enum.TaskStatus) {
        task.isDirty = true

        taskRepository.updateTaskStatus(task, changeToStatus, callback = {
            _taskDetailResult.value = TaskResult(success = "Sub task status has been changed to ${changeToStatus.string}")
        })
    }

}
