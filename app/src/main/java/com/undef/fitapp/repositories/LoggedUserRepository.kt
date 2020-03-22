package com.undef.fitapp.repositories

import com.undef.fitapp.models.UserData

class LoggedUserRepository {
    var preRegUserData = UserData("","",0.0,"","","","",0.0, "")

    var user: UserData? = null

    companion object {
        val instance = LoggedUserRepository()
    }



}