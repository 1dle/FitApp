package com.undef.fitapp.ui.diary

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.undef.fitapp.models.Daily
import com.undef.fitapp.models.Food
import com.undef.fitapp.models.FoodNMet
import com.undef.fitapp.requests.FitAppServerApi
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class SearchMnEViewModel: ViewModel() {

    suspend fun getSearchResult(top: Int, query: String){
        val retrofit = Retrofit.Builder()
            .baseUrl("http://10.0.2.2:45455")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val service: FitAppServerApi = retrofit.create(FitAppServerApi::class.java)

        val valuesToPost = HashMap<String, Any>()
        valuesToPost.put("Top",top)
        valuesToPost.put("Query", query)

        val call = service.searchFood(valuesToPost)

        call.enqueue( object : Callback<List<Food>> {
            override fun onFailure(call: Call<List<Food>>, t: Throwable) {
                print(call.toString())
            }

            override fun onResponse(call: Call<List<Food>>, response: Response<List<Food>>) {
                if(response.isSuccessful){
                    _results.apply {
                        postValue(response.body()!!.toMutableList())
                    }
                }
            }

        }
        )
    }
    private val _results = MutableLiveData<MutableList<Food>>().apply {
        value = mutableListOf<Food>()
    }
    val searchResults: LiveData<MutableList<Food>> = _results
}