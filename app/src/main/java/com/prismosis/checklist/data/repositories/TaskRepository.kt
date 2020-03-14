package com.prismosis.checklist.data.repositories

import androidx.lifecycle.LiveData
import android.content.Context
import com.prismosis.checklist.data.Result
import com.prismosis.checklist.data.database.AppDatabase
import com.prismosis.checklist.data.model.Task
import com.prismosis.checklist.data.model.TaskDao
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

/**
 * Created by Ehsan Saddique on 2020-03-13
 */

class TaskRepository(database: AppDatabase) {

    private var taskDao: TaskDao

    val allTasks: LiveData<List<Task>>

    init {
        taskDao = database.taskDao()
        allTasks = taskDao.getAllTasks()
    }

    fun insertTask(task: Task, callback: (Result<String>)->Unit) {
        GlobalScope.launch {
            taskDao.insert(task)

            GlobalScope.launch(Dispatchers.Main) {
                callback.invoke(Result.Success("Task has been added"))
            }
        }
    }

    fun updateTask(task: Task, callback: (Result<String>)->Unit) {
        GlobalScope.launch {
            taskDao.update(task)

            GlobalScope.launch(Dispatchers.Main) {
                callback.invoke(Result.Success("Task has been updated"))
            }
        }
    }

}