package com.prismosis.checklist.ui.task

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.prismosis.checklist.data.database.AppDatabase
import com.prismosis.checklist.data.repositories.TaskRepository
import com.prismosis.checklist.data.repositories.UserRepository
import com.prismosis.checklist.networking.RestClient

class TaskUpdateViewModelFactory : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TaskUpdateViewModel::class.java)) {
            return TaskUpdateViewModel(
                taskRepository = TaskRepository(AppDatabase.getAppDataBase(), RestClient())
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
