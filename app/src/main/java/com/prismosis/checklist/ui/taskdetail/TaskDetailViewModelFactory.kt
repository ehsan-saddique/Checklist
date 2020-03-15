package com.prismosis.checklist.ui.taskdetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.prismosis.checklist.data.database.AppDatabase
import com.prismosis.checklist.data.repositories.TaskRepository
import com.prismosis.checklist.data.repositories.UserRepository
import com.prismosis.checklist.networking.RestClient

class TaskDetailViewModelFactory : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TaskDetailViewModel::class.java)) {
            return TaskDetailViewModel(
                taskRepository = TaskRepository(AppDatabase.getAppDataBase(), RestClient())
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
