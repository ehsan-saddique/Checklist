package com.prismosis.checklist.ui.task

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.prismosis.checklist.data.database.AppDatabase
import com.prismosis.checklist.data.repositories.TaskRepository
import com.prismosis.checklist.data.repositories.UserRepository
import com.prismosis.checklist.networking.RestClient

//class TaskViewModelFactory : ViewModelProvider.Factory {
//
//    @Suppress("UNCHECKED_CAST")
//    override fun <T : ViewModel> create(modelClass: Class<T>): T {
//        if (modelClass.isAssignableFrom(TaskViewModel::class.java)) {
//            return TaskViewModel(
//                taskRepository = TaskRepository(AppDatabase.getAppDataBase(), RestClient()),
//                userRepository = UserRepository()
//            ) as T
//        }
//        throw IllegalArgumentException("Unknown ViewModel class")
//    }
//}
