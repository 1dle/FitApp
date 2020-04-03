package com.undef.fitapp.ui.diary

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.undef.fitapp.models.Daily
import com.undef.fitapp.requests.FitAppServerApi
import com.undef.fitapp.models.Food
import com.undef.fitapp.models.FoodNMet
import com.undef.fitapp.models.Met
import com.undef.fitapp.requests.ConnectionData
import com.undef.fitapp.requests.ConnectionData.retrofit
import com.undef.fitapp.requests.ConnectionData.service
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class DiaryViewModel : ViewModel() {
    /* test
    fun changeConsumedText() {
        _consumedText.apply { value = "asd" }
    }*/
    suspend fun getDailyData(){

        val valuesToPost = HashMap<String, Any>()
        valuesToPost["ID"] = 1
        valuesToPost["Date"] =  "2020-04-03"


        val call = service.getDaily(valuesToPost)

        call.enqueue( object : Callback<Daily>{
            override fun onFailure(call: Call<Daily>, t: Throwable) {
                print(call.toString())
            }

            override fun onResponse(call: Call<Daily>, response: Response<Daily>) {
                if(response.isSuccessful){
                    _consumedText.apply { value = "Consumed: ${response.body()!!.consumed} kcals"}
                    _remainingText.apply { value = "Remaining: ${response.body()!!.remaining} kcals" }
                    _burnedText.apply { value = "Burned: ${response.body()!!.burned} kcals" }
                    _foodNMet.apply {
                        val newList = mutableListOf<FoodNMet>()
                        newList.addAll(response.body()!!.foods)
                        newList.addAll(response.body()!!.mets)
                        postValue(newList)
                    }
                }
            }

        }
        )
    }

    private val _consumedText = MutableLiveData<String>().apply {
        value = "Consumed: 0 kcals"
    }
    private val _burnedText = MutableLiveData<String>().apply {
        value = "Burned: 0 kcals"
    }
    private val _remainingText = MutableLiveData<String>().apply {
        value = "Remaining: 0 kcals"
    }
    private val _foodNMet = MutableLiveData<MutableList<FoodNMet>>().apply {
        value = mutableListOf<FoodNMet>()
    }

    val consumedText: LiveData<String> = _consumedText
    val burnedText: LiveData<String> = _burnedText
    val remainingText: LiveData<String> = _remainingText
    val foodNMet: LiveData<MutableList<FoodNMet>> = _foodNMet





}