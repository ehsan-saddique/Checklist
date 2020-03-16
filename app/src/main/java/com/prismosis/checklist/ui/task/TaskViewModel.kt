package com.prismosis.checklist.ui.task

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.prismosis.checklist.data.model.DTOTask
import com.prismosis.checklist.data.repositories.TaskRepository
import com.prismosis.checklist.utils.Enum
import com.prismosis.checklist.data.Result
import com.prismosis.checklist.data.repositories.UserRepository
import com.prismosis.checklist.utils.Utils
import javax.inject.Inject

class TaskViewModel @Inject constructor (private val taskRepository: TaskRepository, private val userRepository: UserRepository) : ViewModel() {

    private val _taskResult = MutableLiveData<TaskResult>()
    val taskResult: LiveData<TaskResult> = _taskResult

    fun getAllTasks(): LiveData<List<DTOTask>> {
        return taskRepository.getAllTasks()
    }

    fun getDirtyTasksCount(): LiveData<Int> {
        return taskRepository.getDirtyTasksCount()
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

    fun logoutUser() {
        userRepository.logout()
        deleteAllTasks()
    }

    fun deleteAllTasks() {
        taskRepository.deleteAllTasks()
    }

    fun syncDataWithCloud() {
        taskRepository.uploadTasksToCloud { result ->
            if (result is Result.Success) {
                _taskResult.value = TaskResult(success = result.data)
            }
            else {
                _taskResult.value = TaskResult(error = (result as Result.Error).exception.localizedMessage)
            }
        }
    }

    fun fetchDataFromCloud() {
        taskRepository.fetchTasksFromCloud { result ->
            if (result is Result.Success) {
                Utils.setIsTasksFetched(true)
                _taskResult.value = TaskResult(success = result.data)
            }
            else {
                _taskResult.value = TaskResult(error = (result as Result.Error).exception.localizedMessage)
            }
        }
    }

    fun forceSyncData() {
        taskRepository.uploadTasksToCloud { uploadResult ->
            if (uploadResult is Result.Success) {
                taskRepository.fetchTasksFromCloud { downloadResult ->
                    if (downloadResult is Result.Success) {
                        _taskResult.value = TaskResult(success = downloadResult.data)

                        }
                    else {
                        _taskResult.value = TaskResult(error = (downloadResult as Result.Error).exception.localizedMessage)
                    }
                }
            }
            else {
                _taskResult.value = TaskResult(error = (uploadResult as Result.Error).exception.localizedMessage)
            }
        }
    }


}
