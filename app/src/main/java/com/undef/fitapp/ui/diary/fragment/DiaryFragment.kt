package com.undef.fitapp.ui.diary.fragment

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.datetime.datePicker
import com.getbase.floatingactionbutton.FloatingActionButton
import com.github.zawadz88.materialpopupmenu.popupMenu
import com.undef.fitapp.R
import com.undef.fitapp.api.model.Food
import com.undef.fitapp.api.model.GpsExercise
import com.undef.fitapp.api.model.Met
import com.undef.fitapp.api.repositories.UserDataRepository
import com.undef.fitapp.api.repositories.toCalendar
import com.undef.fitapp.api.service.ConnectionData
import com.undef.fitapp.api.service.ConnectionData.postExerciseToServer
import com.undef.fitapp.custom.ItemType
import com.undef.fitapp.custom.MEListAdapter
import com.undef.fitapp.custom.SearchMode
import com.undef.fitapp.ui.diary.EditMealActivity
import com.undef.fitapp.ui.diary.SearchMnEActivity
import com.undef.fitapp.ui.diary.viewmodel.DiaryViewModel
import kotlinx.android.synthetic.main.fragment_diary.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.awaitResponse

class DiaryFragment : Fragment(), MEListAdapter.OnMEListItemClickListener{

    private lateinit var diaryViewModel: DiaryViewModel

    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        /*return inflater.inflate(R.layout.fragment_diary, container, false)*/
        diaryViewModel = ViewModelProviders.of(this).get(DiaryViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_diary, container, false)

        val liveDailyStats = mutableListOf<Pair<TextView, LiveData<String>>>(
            // Pair ( UI element name , property name in ViewModel )
            Pair(root.findViewById(R.id.tvConsumed), diaryViewModel.consumedText),
            Pair(root.findViewById(R.id.tvBurned), diaryViewModel.burnedText),
            Pair(root.findViewById(R.id.tvRemaining), diaryViewModel.remainingText)
        )

        liveDailyStats.forEach { pit ->
            pit.second.observe(viewLifecycleOwner, Observer {
                pit.first.text = it
            })
        }


        diaryViewModel.selectedDate.observe(viewLifecycleOwner, Observer {
            tvDiaryDate.text = diaryViewModel.selectedDateAsString()
        })


        recyclerView = root.findViewById(R.id.rvDiaryDaily)
        diaryViewModel.foodNMet.observe(viewLifecycleOwner, Observer { fnm ->
            viewManager = LinearLayoutManager(context)
            viewAdapter = MEListAdapter(fnm, SearchMode.NONE,this)
            recyclerView.layoutManager = viewManager
            recyclerView.adapter = viewAdapter
        })

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //Log.d("LOGGED_USER", UserDataRepository.loggedUser.toString())

        CoroutineScope(Dispatchers.IO).launch {
            diaryViewModel.getDailyData()
        }

        val fabAddMeal: FloatingActionButton = view.findViewById(R.id.fabAddMeal)
        fabAddMeal.setOnClickListener {
            //Toast.makeText(this,"addMeal", Toast.LENGTH_SHORT).show();
            val intent = Intent(activity, SearchMnEActivity::class.java)
            intent.putExtra("search_mode", SearchMode.MEAL)
            intent.putExtra("add_date", diaryViewModel.selectedDateAsString(DiaryViewModel.DatePurpose.SERVER))
            startActivity(intent)
            fab.collapse()
        }
        val fabAddExercise: FloatingActionButton = view.findViewById(R.id.fabAddExercise)
        fabAddExercise.setOnClickListener {
            val intent = Intent(activity, SearchMnEActivity::class.java)
            intent.putExtra("search_mode", SearchMode.EXERCISE)
            intent.putExtra("add_date", diaryViewModel.selectedDateAsString(DiaryViewModel.DatePurpose.SERVER))
            startActivity(intent)
            fab.collapse()
        }

        btnPrevDate.setOnClickListener {
            diaryViewModel.incrementDate(-1)
        }
        btnNextDate.setOnClickListener {
            diaryViewModel.incrementDate(1)
        }
        tvDiaryDate.setOnClickListener {
            MaterialDialog(view.context).show {

                datePicker(currentDate = diaryViewModel.selectedDate.value!!.toCalendar() ){ dialog, datetime ->
                    diaryViewModel.setDate(datetime.time)
                }
            }
        }


    }

    override fun onResume() {
        super.onResume()
        CoroutineScope(Dispatchers.IO).launch {
            diaryViewModel.getDailyData()
        }
/*
        if(activity!!.intent.extras != null)
            if(activity!!.intent.hasExtra("where"))
                if(activity!!.intent.getStringExtra("where") == "addmeal"){
                    //frissites
                    CoroutineScope(Dispatchers.IO).launch {
                        diaryViewModel.getDailyData()
                    }
                }*/
    }

    @SuppressLint("SetTextI18n")
    override fun onMEListItemClick(position: Int, view: View) {
        if(diaryViewModel.foodNMet.value!!.get(position).type != ItemType.GPSEX){
            popupMenu {
                dropdownGravity = Gravity.RIGHT
                dropDownHorizontalOffset = -10
                dropDownVerticalOffset = 10
                section {
                    item {
                        label = "Modify"
                        icon = R.drawable.ic_mode_edit
                        callback = {
                            //on click
                            if(diaryViewModel.foodNMet.value!!.get(position).type == ItemType.EXERCISE){
                                //ha exerciseot akarok módosítani

                                //COPIED FROM SEARCHMNEACTIVITY
                                val exercise = diaryViewModel.foodNMet.value!!.get(position) as Met

                                val dialog = MaterialDialog(context!!)
                                    .noAutoDismiss() //dialog doesn't disappear when touch outside of the dialog
                                    .customView(R.layout.dialog_edit_exercise)



                                dialog.findViewById<TextView>(R.id.tvDialogExerciseTitle).text = "Edit exercise"
                                dialog.findViewById<EditText>(R.id.etDialogExerciseMinutes).setText(exercise.duration.toString()) //previous amount

                                val tvBurned = dialog.findViewById<TextView>(R.id.tvDialogExerciseBurnedKcals)
                                tvBurned.setText("${(exercise.duration * exercise.metNum * UserDataRepository.loggedUser.weight * 3.5) / 200} kcals burned")//previous amount


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
                                val dateAndTime = exercise.date!!.split("T")
                                val times = dateAndTime[1].split(":")
                                val old_time = times[0]+":"+times[1]
                                dialog.findViewById<EditText>(R.id.etDialogExerciseTimestamp).setText(old_time)

                                dialog.findViewById<TextView>(R.id.tvDialogExerciseDate).text = dateAndTime[0]
                                dialog.findViewById<TextView>(R.id.btnExerciseDialogAdd).text = "Save exercise"


                                dialog.findViewById<TextView>(R.id.btnExerciseDialogAdd).setOnClickListener {
                                    if(!dialog.findViewById<EditText>(R.id.etDialogExerciseMinutes).text.isNullOrEmpty() && !dialog.findViewById<EditText>(R.id.etDialogExerciseTimestamp).text.isNullOrEmpty()){
                                        CoroutineScope(Dispatchers.IO).launch{
                                            //met_id megszerzése
                                            val metId = getMetIdFormName(exercise.detailed)
                                            if(metId != -1){
                                                //ha megvan a metid

                                                //elöző excercize törlése
                                                val resp = ConnectionData.service.deleteExercise((diaryViewModel.foodNMet.value!!.get(position) as Met).id).awaitResponse()
                                                if(resp.isSuccessful()){
                                                    val statusCode =
                                                        postExerciseToServer(
                                                            UserDataRepository.loggedUser.id,
                                                            metId,
                                                            exercise.date!!,
                                                            dialog.findViewById<EditText>(R.id.etDialogExerciseMinutes).text.toString().toDouble())

                                                    if(statusCode == 1){
                                                        //sikeres insert
                                                        dialog.dismiss()
                                                        diaryViewModel.getDailyData()
                                                    }else{
                                                        TODO("sikertelen insert")
                                                    }
                                                    diaryViewModel.getDailyData()
                                                }else{
                                                    TODO("Ha nem sikeres a törlés")
                                                }

                                            }else{
                                                //nincs meg  amet id
                                            }
                                        }
                                    }
                                }
                                dialog.findViewById<TextView>(R.id.btnExerciseDialogCancel).setOnClickListener {
                                    dialog.dismiss()
                                }
                                dialog.show ()



                            }else{
                                //ha kaja módosítás
                                CoroutineScope(Dispatchers.IO).launch {
                                    val f = foodToModifyData((diaryViewModel.foodNMet.value!!.get(position) as Food).name)
                                    if(f != null){
                                        val intent = Intent(context, EditMealActivity::class.java)
                                        val bundle = Bundle()
                                        bundle.putParcelable("selected_food",f as Food)
                                        intent.putExtra("myBundle",bundle)
                                        intent.putExtra("add_date", (diaryViewModel.foodNMet.value!!.get(position) as Food).date)
                                        intent.putExtra("old_amount", (diaryViewModel.foodNMet.value!!.get(position) as Food).quantity)
                                        intent.putExtra("meal_id_delete", (diaryViewModel.foodNMet.value!!.get(position) as Food).id)
                                        startActivity(intent)
                                    }
                                }

                            }
                        }
                    }
                    item {
                        label = "Delete"
                        icon = R.drawable.ic_delete
                        labelColor = Color.parseColor("#DD000A")
                        callback = {
                            //on click
                            if(diaryViewModel.foodNMet.value!!.get(position).type == ItemType.EXERCISE){
                                //ha exercise törlsé
                                MaterialDialog(context!!).show {
                                    title (text = "Delete exercise")
                                    message(text = "Delete \"${(diaryViewModel.foodNMet.value!!.get(position) as Met).detailed}\"\nAre you sure?")
                                    positiveButton(text = "Yes"){
                                        CoroutineScope(Dispatchers.IO).launch {
                                            val resp = ConnectionData.service.deleteExercise((diaryViewModel.foodNMet.value!!.get(position) as Met).id).awaitResponse()
                                            if(resp.isSuccessful()){
                                                diaryViewModel.getDailyData()
                                            }else{
                                                TODO("Ha nem sikeres a törlés")
                                            }
                                        }
                                    }
                                    negativeButton(text = "No") { dismiss() }
                                }
                            }else{
                                //ha meal törlés
                                MaterialDialog(context!!).show {
                                    title (text = "Delete meal")
                                    message(text = "Delete \"${(diaryViewModel.foodNMet.value!!.get(position) as Food).name}\"\nAre you sure?")
                                    positiveButton(text = "Yes"){
                                        CoroutineScope(Dispatchers.IO).launch {
                                            val resp = ConnectionData.service.deleteMeal((diaryViewModel.foodNMet.value!!.get(position) as Food).id).awaitResponse()
                                            if(resp.isSuccessful()){
                                                diaryViewModel.getDailyData()
                                            }else{
                                                TODO("Ha nem sikeres a törlés")
                                            }
                                        }
                                    }
                                    negativeButton(text = "No") { dismiss() }
                                }

                            }

                        }
                    }
                }
            }.show(context!!,view)
        }else{
            //ha gps exercisera kattiontok akkor lehessen törölni
            popupMenu {
                dropdownGravity = Gravity.RIGHT
                dropDownHorizontalOffset = -10
                dropDownVerticalOffset = 10
                section {
                    item {
                        label = "Delete"
                        icon = R.drawable.ic_delete
                        labelColor = Color.parseColor("#DD000A")
                        callback = {
                            MaterialDialog(context!!).show {
                                title (text = "Delete GPS exercise")
                                message(text = "Delete \"${(diaryViewModel.foodNMet.value!!.get(position) as GpsExercise).getShortTitle()}\"\nAre you sure?")
                                positiveButton(text = "Yes"){
                                    CoroutineScope(Dispatchers.IO).launch {
                                        val resp = ConnectionData.service.deleteGpsExercise((diaryViewModel.foodNMet.value!!.get(position) as GpsExercise).userID).awaitResponse()
                                        if(resp.isSuccessful){
                                            diaryViewModel.getDailyData()
                                        }else{
                                            TODO("Ha nem sikeres a törlés")
                                        }
                                    }
                                }
                                negativeButton(text = "No") { dismiss() }
                            }
                        }
                    }
                }
            }.show(context!!,view)
        }
    }
    private suspend fun foodToModifyData(foodName: String): Food? {
        val toPost = HashMap<String, Any>()
        toPost["Top"] = 1;
        toPost["Query"] = foodName
        val call = ConnectionData.service.searchFood(toPost).awaitResponse()
        if(call.isSuccessful && !call.body()!!.isNullOrEmpty()){
            return call.body()!![0]
        }else{
            return null
        }
    }
    private suspend fun getMetIdFormName(metName: String): Int{
        val toPost = HashMap<String, Any>()
        toPost["Top"] = 1;
        toPost["Query"] = metName
        val call = ConnectionData.service.searchExercise(toPost).awaitResponse()
        if(call.isSuccessful && !call.body()!!.isNullOrEmpty()){
            return call.body()!![0].id
        }else{
            return -1
        }
    }

}
