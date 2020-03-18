package com.prismosis.checklist.networking

import com.google.firebase.auth.FirebaseAuth
import com.prismosis.checklist.data.model.DTOTask
import com.prismosis.checklist.data.model.TaskListServerResponse
import com.prismosis.checklist.data.model.TaskServerResponse
import com.prismosis.checklist.data.model.TaskServerResponseWrapper
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

/**
 * Created by Ehsan Saddique on 2020-03-15
 */

open class RestClient {
    private var service: TaskService? = null

    fun getTaskService(): TaskService {
        if (service == null) {
            service = Retrofit.Builder()
                .baseUrl("https://firestore.googleapis.com/v1/projects/checklist-c04e2/databases/(default)/documents/checklist/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(TaskService::class.java)
        }

        return service!!
    }
}

interface TaskService {
    @GET("tasks")
    fun getAllTasks(@Header("Authorization") authToken: String): Call<TaskListServerResponse>

    @PATCH("tasks/{userId}/{taskId}")
    fun insertOrUpdateTask(@Header("Authorization") authToken:
                           String, @Path("userId")
                            userId: String,
                           @Path("taskId")
                           taskId: String, @Body parameters: TaskServerResponseWrapper): Call<TaskServerResponse>
}