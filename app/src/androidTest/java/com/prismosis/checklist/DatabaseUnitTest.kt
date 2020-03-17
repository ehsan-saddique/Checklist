package com.prismosis.checklist

import androidx.room.Room
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import com.prismosis.checklist.data.database.AppDatabase
import com.prismosis.checklist.data.model.Task
import com.prismosis.checklist.data.model.TaskDao
import com.prismosis.checklist.utils.ChecklistApplication
import com.prismosis.checklist.utils.Enum
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.util.*
import org.junit.Assert.*

/**
 * Created by Ehsan Saddique on 2020-03-17
 */
@RunWith(AndroidJUnit4ClassRunner::class)
class DatabaseUnitTests {
    lateinit var database: AppDatabase
    lateinit var taskDao: TaskDao
    lateinit var testTask: Task

    private var TASK_ID = "t1a1s1k"
    private var USER_ID = "u1s1e1r"

    @Before
    fun setup() {
        val context = ChecklistApplication.context()
        database = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java).build()
        taskDao = database.taskDao()

        testTask = Task(TASK_ID, "", USER_ID, Date(),
            "Task from unit test", "", Date(), Date(), Enum.TaskStatus.PENDING,
            true, false)
    }

    //INSERT

    @Test
    fun testInsertion_Task() {

        taskDao.insert(testTask)
        assertTrue(taskDao.getTask(TASK_ID).taskId == testTask.taskId)
    }

    @Test
    fun testInsertion_Subtask() {

        taskDao.insert(testTask)

        val subtaskId = TASK_ID+"_subtask"
        val subtask = Task(subtaskId, testTask.taskId, USER_ID, Date(),
            "Sub task from unit test", "", Date(), Date(), Enum.TaskStatus.PENDING,
            true, false)

        taskDao.insert(subtask)

        assertTrue(taskDao.getTask(subtaskId).parentId == testTask.taskId)
    }

    @Test
    fun testInsertion_TaskStatus() {

        testTask.status = Enum.TaskStatus.COMPLETED
        taskDao.insert(testTask)
        assertTrue(taskDao.getTask(TASK_ID).status == Enum.TaskStatus.COMPLETED)
    }

    @Test
    fun testInsertion_TaskAgainstUserId() {

        taskDao.insert(testTask)
        assertTrue(taskDao.getTask(TASK_ID).userId == USER_ID)
    }

    //UPDATE

    @Test
    fun testUpdation_InsertOrUpdate() {
        taskDao.insertOrUpdate(testTask)
        val updatedName = "Task name updated"
        testTask.taskName = "Task name updated"
        taskDao.insertOrUpdate(testTask)

        assertTrue(taskDao.getTask(TASK_ID).taskName == updatedName)
    }

    @Test
    fun testUpdation_TaskName() {
        taskDao.insert(testTask)

        val updatedName = "Task name updated"
        testTask.taskName = updatedName
        taskDao.update(testTask)

        assertTrue(taskDao.getTask(TASK_ID).taskName == updatedName)
    }

    @Test
    fun testUpdation_TaskDescription() {
        taskDao.insert(testTask)

        val updatedDescription = "Task description updated"
        testTask.taskDescription = updatedDescription
        taskDao.update(testTask)

        assertTrue(taskDao.getTask(TASK_ID).taskDescription == updatedDescription)
    }

    @Test
    fun testUpdation_TaskStartDate() {
        taskDao.insert(testTask)

        val startDate = Date()
        testTask.startDate = startDate
        taskDao.update(testTask)

        assertTrue(taskDao.getTask(TASK_ID).startDate == startDate)
    }

    @Test
    fun testUpdation_TaskEndDate() {
        taskDao.insert(testTask)

        val endDate = Date()
        testTask.endDate = endDate
        taskDao.update(testTask)

        assertTrue(taskDao.getTask(TASK_ID).endDate == endDate)
    }

    //SELECT

    fun testSelection_GetTask() {
        taskDao.insert(testTask)
        val task = taskDao.getTask(TASK_ID)

        assertTrue(task.taskId == TASK_ID)
    }

    fun testSelection_GetAllTasks() {
        taskDao.insert(testTask)
        testTask.taskId = testTask.taskId + "_newtask"
        taskDao.insert(testTask)

        val allTasks = taskDao.getAllTasks()

        assertNotNull(allTasks.value)
        assertTrue(allTasks.value!!.size == 2)
    }

    fun testSelection_GetParentTask() {
        taskDao.insert(testTask)

        val subtaskId = TASK_ID+"_subtask"
        val subtask = Task(subtaskId, testTask.taskId, USER_ID, Date(),
            "Sub task from unit test", "", Date(), Date(), Enum.TaskStatus.PENDING,
            true, false)

        taskDao.insert(subtask)

        assertTrue(taskDao.getParentTask(subtaskId)!!.taskId == testTask.taskId)
    }
}