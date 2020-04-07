package com.undef.fitapp.api.service

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ConnectionData {
    private const val url = "http://10.0.2.2:45455"
    val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(url)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    val service: FitAppServerApi = retrofit.create(FitAppServerApi::class.java)
}