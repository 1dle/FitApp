package com.undef.fitapp.requests

import com.undef.fitapp.models.Daily
import com.undef.fitapp.models.Food
import com.undef.fitapp.models.FoodNMet
import com.undef.fitapp.models.Met
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

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
}