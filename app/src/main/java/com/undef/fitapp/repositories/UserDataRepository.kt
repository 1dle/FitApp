package com.undef.fitapp.repositories

import com.undef.fitapp.models.UserData

object UserDataRepository {
    var preRegUserData = UserData("","",0.0,"","","","",0.0, "")

    lateinit var loggedUser: UserData


}