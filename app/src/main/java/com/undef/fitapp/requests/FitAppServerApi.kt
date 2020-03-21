package com.undef.fitapp.requests

import com.undef.fitapp.models.Daily
import retrofit2.Call
import retrofit2.http.GET

interface FitAppServerApi {
    //ip:port/api/Daily
    //@GET("Daily")
    @GET("daily.json")
    fun getDaily(): Call<Daily>
}