package com.prismosis.checklist

import android.app.Activity
import android.net.Uri
import android.os.Parcel
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.internal.firebase_auth.zzff
import com.google.android.gms.tasks.*
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.*
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.doAnswer
import com.nhaarman.mockitokotlin2.verify
import com.prismosis.checklist.data.database.AppDatabase
import com.prismosis.checklist.data.model.DTOTask
import com.prismosis.checklist.data.model.TaskDao
import com.prismosis.checklist.data.repositories.TaskRepository
import com.prismosis.checklist.networking.RestClient
import com.prismosis.checklist.utils.Enum
import org.junit.Before
import org.junit.Test
import org.junit.Rule
import org.junit.Assert.*
import org.mockito.*
import org.mockito.ArgumentMatchers.any
import java.lang.Exception
import java.util.*
import java.util.concurrent.Executor
import kotlin.collections.HashMap
import com.google.firebase.auth.FirebaseAuth
import com.prismosis.checklist.utils.Utils
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.mockito.ArgumentMatchers.eq
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import org.mockito.invocation.InvocationOnMock
import kotlin.collections.ArrayList


/**
 * Created by Ehsan Saddique on 2020-03-17
 */

class TaskRepositoryTest constructor() {

    @Mock
    lateinit var database: AppDatabase
    @Mock
    lateinit var restClient: RestClient
    @Mock
    lateinit var firebaseAuth: FirebaseAuth
    @Mock
    lateinit var taskDao: TaskDao


    lateinit var testTask: DTOTask
    lateinit var taskRepository: TaskRepository


    @get:Rule
    val rule = InstantTaskExecutorRule()


    private var TASK_ID = "t1a1s1k"
    private var USER_ID = "u1s1e1r"


    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)
        Mockito.`when`(database.taskDao()).thenReturn(taskDao)
        Mockito.`when`(firebaseAuth.currentUser).thenReturn(mock(FirebaseUser::class.java))
        taskRepository = TaskRepository(database, restClient, firebaseAuth)

        testTask = DTOTask(0, TASK_ID, "", USER_ID,
            "Task from unit test", "", Date(), Date(), Date(), Enum.TaskStatus.PENDING,
            true, false)
    }

    @Test
    fun test_GetAllTasks() {
        val mutableData = MutableLiveData<List<DTOTask>>()
        mutableData.value = listOf(testTask, testTask, testTask)
        val mockedList: LiveData<List<DTOTask>> = mutableData
        Mockito.`when`(taskDao.getAllTasks()).thenReturn(mockedList)

        val allTasks = taskRepository.getAllTasks()
        assertTrue(allTasks.value!!.size == 3)

    }

    @Test
    fun test_UpdateTaskStatusHierarchy() {
        testTask.status = Enum.TaskStatus.INPROGRESS
        taskRepository.updateTaskStatus(testTask, Enum.TaskStatus.COMPLETED) { result ->
            assertTrue(testTask.status == Enum.TaskStatus.COMPLETED)
        }

    }

//    @Test
//    fun test_Fetch()  {
//
//        val callback = com.nhaarman.mockitokotlin2.mock<(String) -> Unit>()
//        Mockito.`when`(utils.getFirebaseAuthToken(callback)).thenAnswer {
//            callback("43")
//        }
//        testTask.status = Enum.TaskStatus.INPROGRESS
//
//        val myScope = GlobalScope
//        runBlocking {
//            myScope.launch {
//                taskRepository.fetchTasksFromCloud() { result ->
//                    val str = result.toString()
//                }
//            }
//        }
//
//    }

}