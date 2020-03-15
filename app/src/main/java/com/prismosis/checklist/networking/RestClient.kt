package com.prismosis.checklist.networking

import com.google.firebase.auth.FirebaseAuth
import com.prismosis.checklist.data.model.DTOTask
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

/**
 * Created by Ehsan Saddique on 2020-03-15
 */

class RestClient {
    private var service: TaskService? = null

    fun getTaskService(): TaskService {
        if (service == null) {
            val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
            service = Retrofit.Builder()
                .baseUrl("https://firestore.googleapis.com/v1/projects/checklist-c04e2/databases/(default)/documents/checklist/$userId/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(TaskService::class.java)
        }

        return service!!
    }
}

interface TaskService {
    @GET("tasks")
    fun getAllTasks(@Header("Authorization") authToken: String): Call<HashMap<String, Any>>

    @PATCH("tasks/{taskId}")
    fun insertOrUpdateTask(@Header("Authorization") authToken:
                           String, @Path("taskId")
                            taskId: String, @Body parameters: HashMap<String, Any>): Call<HashMap<String, Any>>
}