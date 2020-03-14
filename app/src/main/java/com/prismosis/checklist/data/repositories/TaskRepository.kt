package com.prismosis.checklist.data.repositories

import androidx.lifecycle.LiveData
import android.content.Context
import com.prismosis.checklist.data.Result
import com.prismosis.checklist.data.database.AppDatabase
import com.prismosis.checklist.data.model.DTOTask
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

    init {
        taskDao = database.taskDao()
    }

    fun insertTask(task: DTOTask, callback: (Result<String>)->Unit) {
        GlobalScope.launch {
            taskDao.insert(task.getTaskEntity())

            GlobalScope.launch(Dispatchers.Main) {
                callback.invoke(Result.Success("Task has been added"))
            }
        }
    }

    fun updateTask(task: DTOTask, callback: (Result<String>)->Unit) {
        GlobalScope.launch {
            taskDao.update(task.getTaskEntity())

            GlobalScope.launch(Dispatchers.Main) {
                callback.invoke(Result.Success("Task has been updated"))
            }
        }
    }

    fun getAllTasks(): LiveData<List<DTOTask>> {
        return taskDao.getAllTasks()
    }

    fun getAllSubTasks(taskId: String): LiveData<List<DTOTask>> {
        return taskDao.getAllSubTasks(taskId)
    }

}