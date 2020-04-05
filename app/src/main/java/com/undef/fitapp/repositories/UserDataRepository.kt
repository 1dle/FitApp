package com.undef.fitapp.repositories

import com.undef.fitapp.models.UserData

object UserDataRepository {
    var preRegUserData = UserData(0,"","",0.0,"","","","",0.0, "")

    lateinit var loggedUser: UserData

    //todo:: test user datas
    init {
        loggedUser = UserData(1,"1999-12-20","a@g.hu",170.0,"male","Joe","1234","2020-04-01",60.0, "lose")
    }

}