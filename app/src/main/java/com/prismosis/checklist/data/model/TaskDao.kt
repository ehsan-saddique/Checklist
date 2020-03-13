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

    @Delete
    fun delete(task: Task)

    @Query("SELECT * FROM tasks")
    fun getAllTasks(): LiveData<List<Task>>
}