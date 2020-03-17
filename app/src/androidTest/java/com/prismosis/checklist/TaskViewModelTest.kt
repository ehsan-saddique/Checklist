package com.prismosis.checklist

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.room.Room
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import com.google.firebase.auth.FirebaseAuth
import com.prismosis.checklist.data.Result
import com.prismosis.checklist.data.database.AppDatabase
import com.prismosis.checklist.data.model.DTOTask
import com.prismosis.checklist.data.model.TaskDao
import com.prismosis.checklist.data.repositories.TaskRepository
import com.prismosis.checklist.data.repositories.UserRepository
import com.prismosis.checklist.networking.RestClient
import com.prismosis.checklist.ui.task.TaskResult
import com.prismosis.checklist.ui.task.TaskViewModel
import com.prismosis.checklist.utils.ChecklistApplication
import com.prismosis.checklist.utils.Enum
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.MockitoAnnotations
import java.util.*

/**
 * Created by Ehsan Saddique on 2020-03-17
 */
@RunWith(AndroidJUnit4ClassRunner::class)
class TaskViewModelTest {

    @Mock
    lateinit var database: AppDatabase
    @Mock
    lateinit var restClient: RestClient
    @Mock
    lateinit var firebaseAuth: FirebaseAuth
    @Mock
    lateinit var userRepository: UserRepository
    @Mock
    lateinit var observerTaskResult: androidx.lifecycle.Observer<TaskResult>
    @Mock
    lateinit var observerTaskList: androidx.lifecycle.Observer<List<DTOTask>>

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    lateinit var testTask: DTOTask
    lateinit var taskRepository: TaskRepository
    lateinit var taskViewModel: TaskViewModel

    private var TASK_ID = "t1a1s1k"
    private var USER_ID = "u1s1e1r"

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)

        testTask = DTOTask(0, TASK_ID, "", USER_ID,
            "Task from unit test", "", Date(), Date(), Date(), Enum.TaskStatus.PENDING,
            true, false)


        taskRepository = MockTaskRepository(database, restClient, firebaseAuth)
        taskViewModel  = TaskViewModel(taskRepository, userRepository)
        taskViewModel.taskResult.observeForever(observerTaskResult)
    }

    @Test
    fun testTaskDeletion_IsLiveDataChanged() {
        taskViewModel.deleteTask(testTask)
        verify(observerTaskResult).onChanged(TaskResult("Task has been deleted", null))
    }

    @Test
    fun testGetAllTasks_NotifiesOnTaskListChange() {
        val originalTasks: LiveData<List<DTOTask>> = taskViewModel.getAllTasks()
        originalTasks.observeForever(observerTaskList)

        val mediator = MediatorLiveData<Unit>()
        mediator.observeForever {  }

        val changedTasks = MutableLiveData<List<DTOTask>>()
        changedTasks.value = listOf(testTask)

        mediator.addSource(originalTasks, { changedTasks.value = it })

        verify(observerTaskList).onChanged(changedTasks.value)
    }

}

class MockTaskRepository(database: AppDatabase, restClient: RestClient, firebaseAuth: FirebaseAuth): TaskRepository(database, restClient, firebaseAuth) {

    override fun updateTask(task: DTOTask, callback: (Result<String>)->Unit) {
        callback(Result.Success("Success"))
    }

    override fun getAllTasks(): LiveData<List<DTOTask>> {
        val mockTask = DTOTask(0, "MockTaskRepository_taskId", "", "MockTaskRepository_userId",
            "Task from mock task repository", "", Date(), Date(), Date(), Enum.TaskStatus.PENDING,
            true, false)

        val mutableData = MutableLiveData<List<DTOTask>>()
        mutableData.value = listOf(mockTask)
        val mockedList: LiveData<List<DTOTask>> = mutableData

        return mockedList
    }

}