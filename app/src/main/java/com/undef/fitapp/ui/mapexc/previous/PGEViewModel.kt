package com.undef.fitapp.ui.mapexc.previous

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.undef.fitapp.api.model.GpsExercise
import com.undef.fitapp.api.model.UserData
import com.undef.fitapp.api.repositories.UserDataRepository
import com.undef.fitapp.api.service.ConnectionData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.awaitResponse

class PGEViewModel: ViewModel(){
    var exercises = MutableLiveData<MutableList<GpsExercise>>().apply {
        value = mutableListOf()
    }

    suspend fun getExercises(){
        //download from server

        //teszteléshez csak egyet hoz le
            val resp = ConnectionData.service.getGpsExercisesForUser(UserDataRepository.loggedUser.id).awaitResponse()
            if(resp.isSuccessful && !resp.body()!!.isNullOrEmpty()){
                exercises.value!!.addAll(resp.body()!!)
                //notify
                exercises.postValue(exercises.value!!)
            }



    }
}