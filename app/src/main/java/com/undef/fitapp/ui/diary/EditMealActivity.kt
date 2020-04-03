package com.undef.fitapp.ui.diary

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.undef.fitapp.HomeActivity
import com.undef.fitapp.R
import com.undef.fitapp.models.Food
import com.undef.fitapp.requests.ConnectionData
import kotlinx.android.synthetic.main.activity_edit_meal.*
import kotlinx.coroutines.*
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.awaitResponse
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap

class EditMealActivity : AppCompatActivity() {

    @SuppressLint("SimpleDateFormat", "SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_meal)

        val bundle = intent.getBundleExtra("myBundle")!!
        val food  = bundle.getParcelable<Food>("selected_food")!!

            tvEditMealName.text = food.name
            tvEditMealCalories.text = "Calories: ${food.calories} kcals"
            tvEditMealProtein.text = "Protein: ${food.protein} g"
            tvEditMealNetCarbs.text = "Net Carbs: ${food.carbs} g"
            tvEditMealFat.text = "Fat: ${food.fat} g"

        val sdf = SimpleDateFormat("HH:mm")
        sdf.timeZone = TimeZone.getTimeZone("GMT");
        etEditMealTimeStamp.setText(sdf.format(Calendar.getInstance().time))

        btnEditMealSave.text = "Add meal"

        btnEditMealSave.setOnClickListener{
            if(etEditMealAmount.text.toString() != ""){
                //post meal to server
                //make object that post

                CoroutineScope(Dispatchers.IO).launch {
                    val statusCode = postMealToServer(1,
                        food.id,
                        etEditMealAmount.text.toString().toDouble(),
                        "2020-04-03")

                    if(statusCode == 1){
                        //ha sikeresen hozzá lett adva
                        withContext(Dispatchers.Main){
                            val intent = Intent(applicationContext, HomeActivity::class.java)
                            intent.flags = (Intent.FLAG_ACTIVITY_CLEAR_TOP
                                    or Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                            intent.putExtra("where","addmeal")
                            startActivity(intent)
                        }
                    }
                }
            }
        }
    }
    suspend fun postMealToServer(personId: Int, foodId: Int, quantity: Double, date: String): Int{

        val valuesToPost = HashMap<String, Any>()
//                {
//                    "Person_ID": 1,
//                    "FoodNutritions_ID": 1,
//                    "Quantity": 1,
//                    "Date": "2020-04-03T17:45:03.106Z"
//                }
        valuesToPost["Person_ID"] = personId
        valuesToPost["FoodNutritions_ID"] = foodId
        valuesToPost["Quantity"] = quantity
        valuesToPost["Date"] = date


        val call = ConnectionData.service.addMealToDiary(valuesToPost).awaitResponse()
        if(call.isSuccessful && call.body()!=null){
            return call.body()!!
        }else{
            return -1
        }

    }
}
