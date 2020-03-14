package com.prismosis.checklist.data.model

import androidx.lifecycle.LiveData
import androidx.room.*

/**
 * Created by Ehsan Saddique on 2020-03-13
 */

@Dao
interface TaskDao {

    @Insert
    fun insert(task: Task)

    @Update
    fun update(task: Task)

    @Query("SELECT COUNT(subtask.id) AS subTasksCount, task._id, task.id, task.parentId, task.name, task.description, task.startDate, task.endDate, task.status, task.isDirty, task.isDeleted FROM tasks as task LEFT JOIN tasks subtask ON task.id = subtask.parentid AND subtask.isDeleted = 0 WHERE (task.parentId IS NULL OR task.parentId = '') AND task.isDeleted = 0 GROUP BY task.id ORDER BY task._id")
    fun getAllTasks(): LiveData<List<DTOTask>>

    @Query("SELECT COUNT(subtask.id) AS subTasksCount, task._id, task.id, task.parentId, task.name, task.description, task.startDate, task.endDate, task.status, task.isDirty, task.isDeleted FROM tasks as task LEFT JOIN tasks subtask ON task.id = subtask.parentid AND subtask.isDeleted = 0 WHERE (task.id = :taskId OR task.parentId = :taskId) AND task.isDeleted = 0 GROUP BY task.id ORDER BY task._id")
    fun getAllSubTasks(taskId: String): LiveData<List<DTOTask>>
}