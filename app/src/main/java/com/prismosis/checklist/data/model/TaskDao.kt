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

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertOrUpdate(task: Task)

    @Update
    fun update(task: Task)

    @Query("SELECT COUNT(subtask.taskId) AS subTasksCount, task.taskId, task.parentId, task.userId, task.createdAt, task.taskName, task.taskDescription, task.startDate, task.endDate, task.status, task.isDirty, task.isDeleted FROM tasks as task LEFT JOIN tasks subtask ON task.taskId = subtask.parentid AND subtask.isDeleted = 0 WHERE (task.parentId IS NULL OR task.parentId = '') AND task.isDeleted = 0 GROUP BY task.taskId ORDER BY task.createdAt")
    fun getAllTasks(): LiveData<List<DTOTask>>

    @Query("SELECT COUNT(subtask.taskId) AS subTasksCount, task.taskId, task.parentId, task.userId, task.createdAt, task.taskName, task.taskDescription, task.startDate, task.endDate, task.status, task.isDirty, task.isDeleted FROM tasks as task LEFT JOIN tasks subtask ON task.taskId = subtask.parentid AND subtask.isDeleted = 0 WHERE (task.taskId = :taskId OR task.parentId = :taskId) AND task.isDeleted = 0 GROUP BY task.taskId ORDER BY task.createdAt")
    fun getAllSubTasks(taskId: String): LiveData<List<DTOTask>>

    @Query("SELECT * FROM tasks WHERE taskId = :taskId")
    fun getTask(taskId: String): DTOTask

    @Query("SELECT * FROM tasks WHERE taskId = (SELECT parentId FROM tasks WHERE taskId = :taskId)")
    fun getParentTask(taskId: String): DTOTask?

    @Query("SELECT * FROM tasks WHERE parentId = :taskId")
    fun getSubTasks(taskId: String): List<DTOTask>

    @Query("SELECT * FROM tasks WHERE parentId = :parentId")
    fun getSiblings(parentId: String): List<DTOTask>

    @Query("DELETE FROM tasks")
    fun deleteAllTasks()

    @Query("SELECT * FROM tasks WHERE isDirty = 1")
    fun getDirtyTasks(): List<DTOTask>

    @Query("SELECT COUNT(taskId) FROM tasks WHERE isDirty = 1")
    fun getDirtyTasksCount(): LiveData<Int>
}