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
import kotlinx.android.synthetic.main.activity_edit_meal.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.*
import kotlin.collections.HashMap


class DiaryViewModel : ViewModel() {
    /* test
    fun changeConsumedText() {
        _consumedText.apply { value = "asd" }
    }*/

    enum class DatePurpose{
        UI,
        SERVER
    }

    private val calendar = Calendar.getInstance()
    private val todaysDate = Calendar.getInstance().time



    suspend fun getDailyData(){

        val valuesToPost = HashMap<String, Any>()
        valuesToPost["ID"] = 1
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
                        val newList = mutableListOf<FoodNMet>()
                        newList.addAll(response.body()!!.foods)
                        newList.addAll(response.body()!!.mets)

                        //order list by date (feltöltéskor nincs felvive a timestamp)
                        newList.apply{
                            sortBy {
                                it.getDateOfAdd().let{
                                    val parser =  SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
                                    parser.parse(it)
                                }
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
        value = todaysDate
    }

    val consumedText: LiveData<String> = _consumedText
    val burnedText: LiveData<String> = _burnedText
    val remainingText: LiveData<String> = _remainingText
    val foodNMet: LiveData<MutableList<FoodNMet>> = _foodNMet
    val selectedDate: LiveData<Date> = _selectedDate

    fun selectedDateAsString(purpose: DatePurpose = DatePurpose.UI): String{
        val sdf = SimpleDateFormat("yyyy-MM-dd")
        sdf.timeZone = TimeZone.getTimeZone("GMT")
        if(_selectedDate.value == todaysDate && purpose == DatePurpose.UI){
            return "Today"
        }else{
            return sdf.format(_selectedDate.value)
        }

    }

    fun incrementDate(increment: Int){
        /*calendar.time = _selectedDate.value

        if(increment == -1){
            //previous day
            calendar.add(Calendar.DATE, -1)
        }else{
            //next day
            calendar.add(Calendar.DATE, 1)
        }
        setDate(calendar.time)*/
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
fun Date.toCalendar(): Calendar? {
    val cal = Calendar.getInstance()
    cal.time = this
    return cal
}