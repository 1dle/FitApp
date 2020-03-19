package com.undef.fitapp.requests

import kotlinx.coroutines.delay
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request


class ApiClient {
    val JSON: MediaType = "application/json; charset=utf-8".toMediaType()
    companion object {
        val client = OkHttpClient()
    }
    /*
    fun getDaily(userId: Int, date: Date): String{
        val toPost = JSONObject();
        toPost.put("ID", userId)
        toPost.put("Date", date.toString())
        val body: RequestBody = toPost.toString().toRequestBody(JSON)
        val request: Request = Request.Builder()
            .url("localhost:8000/daily.json")
            .post(body)
            .build()
        client.newCall(request).execute().use { response -> return response.body!!.string() }
    }*/

    suspend fun simpleGet(url: String?): String? {
        val request: Request = Request.Builder()
            .url("http://192.168.1.21:8000/daily.json")
            .build()
        client.newCall(request).execute().use { response -> return response.body!!.string() }
    }

}
