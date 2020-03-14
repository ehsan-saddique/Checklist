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

    @Query("SELECT * FROM tasks WHERE isDelete = 0")
    fun getAllTasks(): LiveData<List<Task>>
}