package com.prismosis.checklist.networking

import com.google.gson.GsonBuilder

/**
 * Created by Ehsan Saddique on 2020-03-18
 */

import okhttp3.Headers
import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody

/**
 * Created by Ehsan Saddique on 2019-08-07
 */
open class RestClient {

    private val mClient: OkHttpClient = OkHttpClient.Builder().build()


    fun patch(url: String, parameters: Any, authenticationToken: String) : NetworkResponse
    {
        val gson = GsonBuilder().disableHtmlEscaping().create()
        val jsonBody = gson.toJson(parameters)
        val body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), jsonBody)
        val headers = Headers.Builder().set("Authorization", authenticationToken).build()

        val request = Request.Builder()
            .url(url)
            .patch(body)
            .headers(headers)
            .build()

        val response = mClient.newCall(request).execute()
        if (response.isSuccessful) {
            return NetworkResponse(true, response.body()?.string() ?: "", "")
        }
        else {
            return NetworkResponse(false, "" ?: "", response.body()?.string() ?: "")
        }
    }

    fun get(url: String, authenticationToken: String): NetworkResponse
    {
        val headers = Headers.Builder().set("Authorization", authenticationToken).build()

        val request = Request.Builder()
            .url(url)
            .get()
            .headers(headers)
            .build()

        val response = mClient.newCall(request).execute()
        if (response.isSuccessful) {
            return NetworkResponse(true, response.body()?.string() ?: "", "")
        }
        else {
            return NetworkResponse(false, "" ?: "", response.body()?.string() ?: "")
        }
    }

}

