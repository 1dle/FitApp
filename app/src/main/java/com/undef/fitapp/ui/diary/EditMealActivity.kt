package com.undef.fitapp.ui.diary

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.appcompat.app.AppCompatActivity
import com.undef.fitapp.R
import com.undef.fitapp.api.model.Food
import com.undef.fitapp.api.repositories.MyCalendar
import com.undef.fitapp.api.repositories.UserDataRepository
import com.undef.fitapp.api.service.ConnectionData
import kotlinx.android.synthetic.main.activity_edit_meal.*
import kotlinx.coroutines.*
import retrofit2.awaitResponse
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap

class EditMealActivity : AppCompatActivity() {

    enum class EditMode {
        ADD,
        EDIT
    }

    @SuppressLint("SimpleDateFormat", "SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_meal)

        //default mode of activty
        var editMode = EditMode.ADD

        val bundle = intent.getBundleExtra("myBundle")!!
        val food  = bundle.getParcelable<Food>("selected_food")!!
        var addDate = intent.getStringExtra("add_date")
        var old_amount = 0.0
        var amount = 1.0
        var old_time = ""
        var mealToDeleteID = -1;
        if(intent.hasExtra("old_amount")){
            //ha van old_amount akkor az edit mode az edit
            old_amount = intent.getDoubleExtra("old_amount",0.0)
            mealToDeleteID = intent.getIntExtra("meal_id_delete", -1)
            editMode = EditMode.EDIT
            val dateAndTime = addDate!!.split("T")
            //másodpec leszedése favágó módon
            val times = dateAndTime[1].split(":")
            old_time = times[0]+":"+times[1]
            //idő nélküli dátum
            addDate = dateAndTime[0]

            etEditMealAmount.setText(old_amount.times(100).toString())
            amount = old_amount
        }

        //initial values
        tvEditMealName.text = food.name
        tvEditMealCalories.text = "Calories: %.2f kcals".format(food.calories/100*amount)
        tvEditMealProtein.text = "Protein: %.2f g".format(food.protein/100*amount)
        tvEditMealNetCarbs.text = "Net Carbs: %.2f g".format(food.carbs/100*amount)
        tvEditMealFat.text = "Fat: %.2f g".format(food.fat/100*amount)

        etEditMealAmount.addTextChangedListener(object: TextWatcher{
            override fun afterTextChanged(p0: Editable?) {}
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if(s.toString() != ""){
                    amount = s.toString().toDouble()
                    //real time calculate fields
                    tvEditMealCalories.text = "Calories: %.2f kcals".format(food.calories/100*amount)
                    tvEditMealProtein.text = "Protein: %.2f g".format(food.protein/100*amount)
                    tvEditMealNetCarbs.text = "Net Carbs: %.2f g".format(food.carbs/100*amount)
                    tvEditMealFat.text = "Fat: %.2f g".format(food.fat/100*amount)
                }
            }
        })

        when(editMode){
            EditMode.ADD ->  {
                btnEditMealSave.text =  "Add meal"
                etEditMealTimeStamp.setText(MyCalendar.getHourAndMinutes())
            }
            EditMode.EDIT -> {
                btnEditMealSave.text =  "Save"
                etEditMealTimeStamp.setText(old_time)
            }
        }




        tvEditMealDate.text = addDate

        btnEditMealSave.setOnClickListener{
            if(etEditMealAmount.text.toString() != "" && etEditMealTimeStamp.text.toString()!="" ){
                //post meal to server
                //make object that post


                //ha edit mode-ban vagyunk akkor először törölni kell az előzőt

                CoroutineScope(Dispatchers.IO).launch {

                    var deleteSuccessful = true
                    if(editMode == EditMode.EDIT && mealToDeleteID != -1){
                        val resp = ConnectionData.service.deleteMeal(mealToDeleteID).awaitResponse()
                        if(!resp.isSuccessful){
                            deleteSuccessful = false
                        }
                    }

                    if(deleteSuccessful){
                        val statusCode = postMealToServer(UserDataRepository.loggedUser.id,
                            food.id,
                            amount/100.0,
                            //add time to date for later option to sort by date of add
                            "%sT%s:00".format(addDate, etEditMealTimeStamp.text.toString())
                        )

                        if(statusCode == 1){
                            //ha sikeresen hozzá lett adva
                            withContext(Dispatchers.Main){
                                val intent = Intent(applicationContext, HomeActivity::class.java)
                                intent.flags = (Intent.FLAG_ACTIVITY_CLEAR_TOP
                                        or Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                                intent.putExtra("where","addmeal")
                                startActivity(intent)
                            }
                        }else{
                            TODO("Hozzáadás, módosítás nem sikerült!")
                        }
                    }else{
                        TODO("Törlés nem sikült!")
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
