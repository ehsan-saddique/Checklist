package com.prismosis.checklist.data.repositories

import androidx.lifecycle.LiveData
import com.google.firebase.auth.FirebaseAuth
import com.prismosis.checklist.data.Result
import com.prismosis.checklist.data.database.AppDatabase
import com.prismosis.checklist.data.model.DTOTask
import com.prismosis.checklist.data.model.TaskDao
import com.prismosis.checklist.data.model.TaskListServerResponse
import com.prismosis.checklist.data.model.TaskServerResponseWrapper
import com.prismosis.checklist.networking.RestClient
import com.prismosis.checklist.utils.Enum
import com.prismosis.checklist.utils.Utils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

/**
 * Created by Ehsan Saddique on 2020-03-13
 */

open class TaskRepository @Inject constructor(database: AppDatabase, restClient: RestClient, firebaseAuth: FirebaseAuth) {

    private var taskDao: TaskDao
    private var mRestClient: RestClient
    private var mFirebaseAuth: FirebaseAuth

    init {
        taskDao = database.taskDao()
        mRestClient = restClient
        mFirebaseAuth = firebaseAuth
    }

    fun insertTask(task: DTOTask, callback: (Result<String>)->Unit) {
        GlobalScope.launch {
            taskDao.insert(task.getTaskEntity())

            GlobalScope.launch(Dispatchers.Main) {
                callback.invoke(Result.Success("Task has been added"))
            }
        }
    }

    open fun updateTask(task: DTOTask, callback: (Result<String>)->Unit) {
        GlobalScope.launch {
            taskDao.update(task.getTaskEntity())

            GlobalScope.launch(Dispatchers.Main) {
                callback.invoke(Result.Success("Task has been updated"))
            }
        }
    }

    fun deleteAllTasks() {
        GlobalScope.launch {
            taskDao.deleteAllTasks()
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

        val subTasks = taskDao.getSubTasks(rootTask.taskId)
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
            val parentTask = taskDao.getParentTask(leafTask.taskId)
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
                    val parentTask = taskDao.getParentTask(leafTask.taskId)
                    if (parentTask != null) {
                        updateTaskStatusUpstream(parentTask, taskStatus)
                    }
                }
            }
        }
    }

    open fun getAllTasks(): LiveData<List<DTOTask>> {
        return taskDao.getAllTasks()
    }

    fun getDirtyTasksCount(): LiveData<Int> {
        return taskDao.getDirtyTasksCount()
    }

    fun getAllSubTasks(taskId: String): LiveData<List<DTOTask>> {
        return taskDao.getAllSubTasks(taskId)
    }

    fun uploadTasksToCloud(callback: (Result<String>)->Unit) {

        mFirebaseAuth.currentUser?.getIdToken(true)?.addOnCompleteListener { tokenResult ->

            if (tokenResult.isSuccessful) {
                GlobalScope.launch {
                    var allRequestsSuccessful = true
                    var errorMessage = ""
                    var authToken = tokenResult.result?.token ?: ""
                    authToken = "Bearer " + authToken

                    val dirtyTasks = taskDao.getDirtyTasks()
                    for (task in dirtyTasks) {
                        val serverRequest = TaskServerResponseWrapper()
                        serverRequest.fields = task.toServerResponse()
                        val apiCall = mRestClient.getTaskService().insertOrUpdateTask(authToken, task.taskId, serverRequest)

                        val response = apiCall.execute()
                        if (response.isSuccessful) {
                            task.isDirty = false
                            taskDao.update(task.getTaskEntity())
                        }
                        else {
                            allRequestsSuccessful = false
                            errorMessage = response.errorBody()?.string() ?: ""
                            break
                        }
                    }

                    GlobalScope.launch(Dispatchers.Main) {
                        if (allRequestsSuccessful) {
                            callback.invoke(Result.Success("Your data has been synced successfully."))
                        }
                        else {
                            errorMessage = if (errorMessage.isEmpty()) "An unknown error occurred." else errorMessage
                            callback.invoke(Result.Error(Exception(errorMessage)))
                        }
                    }
                }
            }
            else {
                //Error getting authentication token
                callback.invoke(Result.Error(Exception("There was an error performing this operation. If you keep getting this error, please try to logout and relogin.")))
            }

        }
    }

    fun fetchTasksFromCloud(callback: (Result<String>)->Unit) {
        FirebaseAuth.getInstance().currentUser?.getIdToken(true)?.addOnCompleteListener { tokenResult ->

            if (!tokenResult.isSuccessful()) {
                GlobalScope.launch {
                    var isSuccess = true
                    var errorMessage = ""
                    var authToken = tokenResult.result?.token ?: ""
                    authToken = "Bearer " + authToken

                    val apiCall = mRestClient.getTaskService().getAllTasks(authToken)

                    val response = apiCall.execute()
                    if (response.isSuccessful) {
                        for (document in response.body()?.documents ?: ArrayList()) {
                            val dtoTask = DTOTask.getTaskFromServerResponse(document.fields)
                            taskDao.insertOrUpdate(dtoTask.getTaskEntity())
                        }
                    }
                    else {
                        isSuccess = false
                        errorMessage = response.errorBody()?.string() ?: ""
                    }

                    GlobalScope.launch(Dispatchers.Main) {
                        if (isSuccess) {
                            callback.invoke(Result.Success("Your data has been synced successfully."))
                        }
                        else {
                            errorMessage = if (errorMessage.isEmpty()) "An unknown error occurred." else errorMessage
                            callback.invoke(Result.Error(Exception(errorMessage)))
                        }
                    }
                }
            }
            else {
                //Error getting authentication token
                callback.invoke(Result.Error(Exception("There was an error performing this operation. If you keep getting this error, please try to logout and relogin.")))
            }

        }
    }

}


//apiCall.enqueue(object : Callback<HashMap<String, Any>> {
//    override fun onResponse(call: Call<HashMap<String, Any>>, response: Response<HashMap<String, Any>>) {
//        if (response.isSuccessful) {
//            println("Successfully sent task to server")
//        }
//        else {
//            println("Error sending task to server")
//        }
//    }
//    override fun onFailure(call: Call<HashMap<String, Any>>, t: Throwable) {
//        println("Error sending task to server. ${t.localizedMessage}")
//    }
//})