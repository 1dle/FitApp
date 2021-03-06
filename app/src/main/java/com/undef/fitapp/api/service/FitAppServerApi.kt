package com.undef.fitapp.api.service

import com.undef.fitapp.api.model.*
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*
import retrofit2.http.Path

interface FitAppServerApi {
    //ip:port/api/Daily
    //@GET("Daily")
   /* @GET("daily.json")
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

    @DELETE("api/Meal/{id}")
    fun deleteMeal(@Path("id") id: Int): Call<ResponseBody> //responseBody = no deserialization

    @DELETE("api/Exercise/{id}")
    fun deleteExercise(@Path("id") id: Int): Call<ResponseBody>

    //GpsExercise part

    @POST("api/GpsExercise")
    fun postGpsExercise(@Body gpsExercise: GpsExercise): Call<Int>

    @GET("api/GpsExercise/{id}")
    fun getOneGpsExercise(@Path("id") id: Int): Call<GpsExercise>

    @GET("api/Person/GpsExercise/{id}")
    fun getGpsExercisesForUser(@Path("id") id: Int): Call<List<GpsExercise>>

    @DELETE("api/GpsExercise/{id}")
    fun deleteGpsExercise(@Path("id") id: Int): Call<Int>
}