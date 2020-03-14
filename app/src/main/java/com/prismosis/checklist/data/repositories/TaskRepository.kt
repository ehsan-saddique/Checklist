package com.prismosis.checklist.data.repositories

import androidx.lifecycle.LiveData
import com.prismosis.checklist.data.Result
import com.prismosis.checklist.data.database.AppDatabase
import com.prismosis.checklist.data.model.DTOTask
import com.prismosis.checklist.data.model.TaskDao
import com.prismosis.checklist.utils.Enum
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

    fun updateTaskStatus(task: DTOTask, taskStatus: Enum.TaskStatus, callback: (Result<String>)->Unit) {
        GlobalScope.launch {
            updateTaskStatusDownstream(task, taskStatus)
            updateTaskStatusUpstream(task, taskStatus)

            GlobalScope.launch(Dispatchers.Main) {
                callback.invoke(Result.Success("Task has been updated"))
            }
        }
    }

    private fun updateTaskStatusDownstream(rootTask: DTOTask, taskStatus: Enum.TaskStatus) {
        rootTask.status = taskStatus
        rootTask.isDirty = true
        taskDao.update(rootTask.getTaskEntity())

        val subTasks = taskDao.getSubTasks(rootTask.id)
        if (!subTasks.isEmpty()) {
            for (subTask in subTasks) {
                updateTaskStatusDownstream(subTask, taskStatus)
            }
        }
    }

    private fun updateTaskStatusUpstream(leafTask: DTOTask, taskStatus: Enum.TaskStatus) {
        leafTask.status = taskStatus
        leafTask.isDirty = true
        taskDao.update(leafTask.getTaskEntity())

        if (taskStatus == Enum.TaskStatus.INPROGRESS) {
            val parentTask = taskDao.getParentTask(leafTask.id)
            if (parentTask != null) {
                updateTaskStatusUpstream(parentTask, taskStatus)
            }
        }
        else {
            val siblingTasks = taskDao.getSiblings(leafTask.parentId)
            if (siblingTasks.size > 1) {
                var allSiblingsHaveSameStatus = true
                for (task in siblingTasks) {
                    if (task.status != taskStatus) {
                        allSiblingsHaveSameStatus = false
                        break
                    }
                }

                if (allSiblingsHaveSameStatus) {
                    val parentTask = taskDao.getParentTask(leafTask.id)
                    if (parentTask != null) {
                        updateTaskStatusUpstream(parentTask, taskStatus)
                    }
                }
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