package com.undef.fitapp.ui.diary.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.undef.fitapp.api.model.Daily
import com.undef.fitapp.api.model.FoodNMet
import com.undef.fitapp.api.repositories.MyCalendar
import com.undef.fitapp.api.repositories.UserDataRepository
import com.undef.fitapp.api.repositories.toCalendar
import com.undef.fitapp.api.repositories.toDateTime
import com.undef.fitapp.api.service.ConnectionData.service
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap


class DiaryViewModel : ViewModel() {

    enum class DatePurpose{
        UI,
        SERVER
    }

    suspend fun getDailyData(){

        val valuesToPost = HashMap<String, Any>()
        valuesToPost["ID"] = UserDataRepository.loggedUser.id
        valuesToPost["Date"] = selectedDateAsString(DatePurpose.SERVER)


        val call = service.getDaily(valuesToPost)

        call.enqueue( object : Callback<Daily>{
            override fun onFailure(call: Call<Daily>, t: Throwable) {
                print(call.toString())
            }

            override fun onResponse(call: Call<Daily>, response: Response<Daily>) {
                if(response.isSuccessful){
                    _consumedText.apply { value = "Consumed: %.2f kcals".format(response.body()!!.consumed)}
                    _remainingText.apply { value = "Remaining: %.2f kcals".format(response.body()!!.remaining) }
                    _burnedText.apply { value = "Burned: %.2f kcals".format(response.body()!!.burned) }
                    _foodNMet.apply {
                        val newList = mutableListOf<FoodNMet>().apply {
                            addAll(response.body()!!.foods)
                            addAll(response.body()!!.mets)
                            addAll(response.body()!!.gpsExercises)
                        }


                        //order list by date (feltöltéskor nincs felvive a timestamp)
                        newList.apply{
                            sortBy {
                                it.getDateOfAdd()!!.toDateTime()
                            }
                        }
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
    private val _selectedDate = MutableLiveData<Date>().apply {
        value = MyCalendar.today
    }

    val consumedText: LiveData<String> = _consumedText
    val burnedText: LiveData<String> = _burnedText
    val remainingText: LiveData<String> = _remainingText
    val foodNMet: LiveData<MutableList<FoodNMet>> = _foodNMet
    val selectedDate: LiveData<Date> = _selectedDate

    fun selectedDateAsString(purpose: DatePurpose = DatePurpose.UI): String{
        if(_selectedDate.value == MyCalendar.today && purpose == DatePurpose.UI){
            return "Today"
        }else{
            return MyCalendar.dateToString(_selectedDate.value!!)
        }

    }

    fun incrementDate(increment: Int){
        setDate(_selectedDate.value!!.toCalendar()!!.let{
            it.add(Calendar.DATE, increment)
            it.time
        })
    }
    fun setDate(date: Date){
        _selectedDate.apply {
            value = date
        }
        CoroutineScope(Dispatchers.IO).launch {
            getDailyData()
        }
    }

}