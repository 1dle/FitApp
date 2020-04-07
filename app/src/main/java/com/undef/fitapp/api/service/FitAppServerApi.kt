package com.undef.fitapp.api.service

import com.undef.fitapp.api.model.*
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface FitAppServerApi {

    /*
    //ip:port/api/Daily
    //@GET("Daily")
    @GET("daily.json")
    fun getDaily(): Call<Daily>*/
    @POST("api/Daily")
    fun getDaily( @Body userIdAndDate: HashMap<String, Any>): Call<Daily>

    @POST("api/Search/Meal")
    fun searchFood(@Body topAndQuery: HashMap<String, Any>): Call<List<Food>>

    @POST("api/Search/Exercise")
    fun searchExercise(@Body topAndQuery: HashMap<String, Any>): Call<List<Met>>

    @POST("api/Meal")
    fun addMealToDiary(@Body addMealData: HashMap<String, Any>): Call<Int>

    @POST("api/Exercise")
    fun addExerciseToDiary(@Body addExerciseData: HashMap<String, Any>): Call<Int>

    @POST("api/Login")
    fun checkLogin(@Body loginData: HashMap<String, Any>): Call<UserData>

    @POST("api/Person")
    fun registerUser(@Body userData: UserData): Call<Int>
}