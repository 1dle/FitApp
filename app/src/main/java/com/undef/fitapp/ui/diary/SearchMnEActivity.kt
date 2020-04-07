package com.undef.fitapp.ui.diary

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.undef.fitapp.R
import com.undef.fitapp.api.model.Food
import com.undef.fitapp.api.repositories.MyCalendar
import com.undef.fitapp.api.repositories.UserDataRepository
import com.undef.fitapp.api.service.ConnectionData
import com.undef.fitapp.custom.MEListAdapter
import com.undef.fitapp.custom.MEListAdapter.OnMEListItemClickListener
import com.undef.fitapp.custom.SearchMode
import com.undef.fitapp.ui.diary.viewmodel.SearchMnEViewModel
import kotlinx.android.synthetic.main.activity_search_mn_e.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.awaitResponse
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap


class SearchMnEActivity() : AppCompatActivity(), OnMEListItemClickListener {
    //Search Meal and Exercise Activity

    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager


    private lateinit var viewModel: SearchMnEViewModel

    private lateinit var searchMode: SearchMode

    private lateinit var addDate: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_mn_e)
        val toolbars: Toolbar = findViewById(R.id.toolbarOnSearch)
        setSupportActionBar(toolbars)
        if(supportActionBar != null){
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            supportActionBar!!.setDisplayShowHomeEnabled(true)
        }
        //inititalize searchMode
        if(intent.extras != null){
            if(intent.hasExtra("search_mode")){
                searchMode = intent.getSerializableExtra("search_mode") as SearchMode
            }
            if(intent.hasExtra("add_date")){
                addDate = intent.getStringExtra("add_date")!!
            }

        }


        viewModel = ViewModelProviders.of(this).get(SearchMnEViewModel::class.java)
        recyclerView = findViewById(R.id.rvResultME)
        viewManager = LinearLayoutManager(this)


        if(searchMode == SearchMode.MEAL){
            //set title of toolbar
            title = "Search Meal"

            viewModel.foodSearchResults.observe(this, Observer { sr ->
                viewAdapter = MEListAdapter(sr, searchMode,this)
                recyclerView.layoutManager = viewManager
                recyclerView.adapter = viewAdapter
            })
        }else{
            title = "Search Exercise"

            viewModel.exerciseSearchResults.observe(this, Observer { sr ->
                viewAdapter = MEListAdapter(sr, searchMode, this)
                recyclerView.layoutManager = viewManager
                recyclerView.adapter = viewAdapter
            })
        }


        btnSearchMnE.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                viewModel.getSearchResult(searchMode,5, etSearchME.text.toString())
            }

        }


    }

    override fun onMEListItemClick(position: Int) {
        when(searchMode){
            SearchMode.MEAL -> if(viewModel.foodSearchResults.value!= null){
                val f = viewModel.foodSearchResults.value!![position]
                //Toast.makeText(this, f.name, Toast.LENGTH_SHORT).show()
                val intent = Intent(this, EditMealActivity::class.java)
                val bundle = Bundle()
                bundle.putParcelable("selected_food",f as Food)
                intent.putExtra("myBundle",bundle)
                intent.putExtra("add_date", addDate)
                startActivity(intent)
            }
            SearchMode.EXERCISE -> if(viewModel.exerciseSearchResults.value!= null){
                //ha exercisera kattintok kereséskor megjelik egy dialog box egy beviteli mezővel és egy időmértékegységeket tartalmazó spinner

                val exercise = viewModel.exerciseSearchResults.value!![position]

                val dialog = MaterialDialog(this)
                    .noAutoDismiss() //dialog doesn't disappear when touch outside of the dialog
                    .customView(R.layout.dialog_edit_exercise)

                val tvBurned = dialog.findViewById<TextView>(R.id.tvDialogExerciseBurnedKcals)
                //set excersise.description
                dialog.findViewById<TextView>(R.id.tvDialogExerciseDescription).text = exercise.getTitle()

                dialog.findViewById<EditText>(R.id.etDialogExerciseMinutes).addTextChangedListener(object :
                    TextWatcher {
                    override fun afterTextChanged(s: Editable?) {}
                    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

                    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                        //item.Duration * (item.MetNum * weight * 3.5) / 200.0
                        if(s.toString()!= ""){
                            val burnedKcals = (s.toString().toDouble() * exercise.metNum * UserDataRepository.loggedUser.weight * 3.5) / 200
                            tvBurned.text = "${burnedKcals} kcals burned"
                        }


                    }
                })
                dialog.findViewById<EditText>(R.id.etDialogExerciseTimestamp).setText(MyCalendar.getHourAndMinutes())

                dialog.findViewById<TextView>(R.id.tvDialogExerciseDate).text = addDate
                dialog.findViewById<TextView>(R.id.btnExerciseDialogAdd).setOnClickListener {
                    if(!dialog.findViewById<EditText>(R.id.etDialogExerciseMinutes).text.isNullOrEmpty() && !dialog.findViewById<EditText>(R.id.etDialogExerciseTimestamp).text.isNullOrEmpty()){
                        /*{
                            "Person_ID": 1,
                            "Mets_ID": 1003,
                            "Date": "2020-04-04T16:24:59.297Z",
                            "Duration": 10
                        }*/
                        /* add time to date
                        SimpleDateFormat("yyyy-MM-dd").parse(addDate).toCalendar().apply {
                                    this!!.set(Calendar.HOUR_OF_DAY, etEditMealTimeStamp.text.toString().split(":")[0].toInt())
                                    this!!.set(Calendar.MINUTE, etEditMealTimeStamp.text.toString().split(":")[1].toInt()) }!!.time)
                         */
                        CoroutineScope(Dispatchers.IO).launch{
                            val statusCode =
                                postExerciseToServer(UserDataRepository.loggedUser.id,
                                    exercise.id,
                                    "${addDate}T${dialog.findViewById<EditText>(R.id.etDialogExerciseTimestamp).text.toString()}:00",
                                    dialog.findViewById<EditText>(R.id.etDialogExerciseMinutes).text.toString().toDouble())

                            if(statusCode == 1){
                                //sikeres insert
                                dialog.dismiss()
                                finish()
                            }
                        }




                    }
                }
                dialog.findViewById<TextView>(R.id.btnExerciseDialogCancel).setOnClickListener {
                    dialog.dismiss()
                }
                dialog.show ()
            }

        }



    }

    private suspend fun postExerciseToServer(personId: Int, metsId: Int, date: String, duration: Double): Int {

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

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
