package com.undef.fitapp.api.service

import retrofit2.Retrofit
import retrofit2.awaitResponse
import retrofit2.converter.gson.GsonConverterFactory

object ConnectionData {
    private const val url = "http://10.0.2.2:45457"
    val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(url)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    val service: FitAppServerApi = retrofit.create(FitAppServerApi::class.java)

    //ez a funkciót több activity is használja
    suspend fun postExerciseToServer(personId: Int, metsId: Int, date: String, duration: Double): Int {

        val valuesToPost = HashMap<String,Any>()
        valuesToPost["Person_ID"] = personId
        valuesToPost["Mets_ID"] = metsId
        valuesToPost["Date"] = date
        valuesToPost["Duration"] = duration

        val call = ConnectionData.service.addExerciseToDiary(valuesToPost).awaitResponse()
        if(call.isSuccessful && call.body()!=null){
            return call.body()!!
        }else{
            return -1
        }
    }

}