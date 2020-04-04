package com.undef.fitapp.ui.diary

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.undef.fitapp.models.Food
import com.undef.fitapp.models.FoodNMet
import com.undef.fitapp.models.Met
import com.undef.fitapp.requests.ConnectionData.service
import com.undef.fitapp.ui.custom.SearchMode
import retrofit2.*

class SearchMnEViewModel: ViewModel() {

    suspend fun getSearchResult(mode: SearchMode, top: Int, query: String){

        val valuesToPost = HashMap<String, Any>()
        valuesToPost.put("Top",top)
        valuesToPost.put("Query", query)

        if(mode == SearchMode.MEAL){
            val call = service.searchFood(valuesToPost)
            call.enqueue( object : Callback<List<Food>> {
                override fun onFailure(call: Call<List<Food>>, t: Throwable) {
                    print(call.toString())
                }

                override fun onResponse(call: Call<List<Food>>, response: Response<List<Food>>) {
                    if(response.isSuccessful){
                        _foodResults.apply {
                            postValue(response.body()!!.toMutableList())
                        }
                    }
                }

            });
        }
        if(mode == SearchMode.EXERCISE){
            val call = service.searchExercise(valuesToPost)
            call.enqueue( object : Callback<List<Met>> {
                override fun onFailure(call: Call<List<Met>>, t: Throwable) {
                    print(call.toString())
                }

                override fun onResponse(call: Call<List<Met>>, response: Response<List<Met>>) {
                    if(response.isSuccessful){
                        _exerciseResults.apply {
                            postValue(response.body()!!.toMutableList())
                        }
                    }
                }

            });
        }
    }

    private val _foodResults = MutableLiveData<MutableList<Food>>().apply {
        value = mutableListOf<Food>()
    }
    private val _exerciseResults = MutableLiveData<MutableList<Met>>().apply {
        value = mutableListOf<Met>()
    }
    val foodSearchResults: LiveData<MutableList<Food>> = _foodResults
    val exerciseSearchResults: LiveData<MutableList<Met>> = _exerciseResults

    /*
    //only one container
    private val _searchResults = MutableLiveData<MutableList<FoodNMet>>().apply {
        value = mutableListOf<FoodNMet>()
    }
    val searchResult: LiveData<MutableList<FoodNMet>> = _searchResults
*/
}