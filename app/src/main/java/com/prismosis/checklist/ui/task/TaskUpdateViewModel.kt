package com.prismosis.checklist.ui.task

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import android.util.Patterns
import com.google.firebase.auth.FirebaseAuth
import com.prismosis.checklist.data.Result

import com.prismosis.checklist.R
import com.prismosis.checklist.data.model.DTOTask
import com.prismosis.checklist.data.model.Task
import com.prismosis.checklist.data.repositories.TaskRepository
import com.prismosis.checklist.data.repositories.UserRepository
import com.prismosis.checklist.utils.Enum
import com.prismosis.checklist.utils.Utils
import java.util.*

class TaskUpdateViewModel(private val taskRepository: TaskRepository) : ViewModel() {

    private val _addEditResult = MutableLiveData<TaskResult>()
    val taskUpdateResult: LiveData<TaskResult> = _addEditResult

    fun addTask(parentId: String?, name: String, description: String, startDate: String, endDate: String) {
        val task = DTOTask(0, UUID.randomUUID().toString(),
            parentId ?: "",
            FirebaseAuth.getInstance().currentUser?.uid ?: "",
            name,
            description,
            Date(),
            Utils.dateFromString(startDate),
            Utils.dateFromString(endDate),
            Enum.TaskStatus.PENDING)

        taskRepository.insertTask(task, callback = { result ->
            _addEditResult.value = TaskResult(success = "Task has been added")
        })
    }

    fun updateTask(task: DTOTask, name: String, description: String, startDate: String, endDate: String, status: Enum.TaskStatus) {
        val taskToUpdate = DTOTask(0, task.taskId,
            task.parentId,
            task.userId,
            name,
            description,
            task.createdAt,
            Utils.dateFromString(startDate),
            Utils.dateFromString(endDate),
            status)

        taskRepository.updateTask(taskToUpdate, callback = { result ->
            _addEditResult.value = TaskResult(success = "Task has been updated")
        })
    }

    fun isFormValid(name: String, startDate: String, endDate: String): Boolean {
        var isValid = true
        if (name.isEmpty()) {
            _addEditResult.value = TaskResult(error = "Name is required")
            isValid = false
        }
        else if (startDate.isEmpty()) {
            _addEditResult.value = TaskResult(error = "Start date is required")
            isValid = false
        }
        else if (endDate.isEmpty()) {
            _addEditResult.value = TaskResult(error = "End date is required")
            isValid = false
        }

        return isValid
    }


}
