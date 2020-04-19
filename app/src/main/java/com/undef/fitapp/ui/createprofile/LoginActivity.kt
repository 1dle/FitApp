package com.undef.fitapp.ui.createprofile

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import com.undef.fitapp.R
import com.undef.fitapp.api.model.UserData
import com.undef.fitapp.api.repositories.UserDataRepository
import com.undef.fitapp.api.service.ConnectionData.service
import com.undef.fitapp.ui.diary.HomeActivity
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.*

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val btnRegister: Button = findViewById(R.id.btnRegister)
        btnRegister.setOnClickListener {
            val intent = Intent(this, CreateProfileActivity::class.java)
            startActivity(intent)
        }
        val btnLogin: Button = findViewById(R.id.btnLogin)

        //fill inputs for testing
        btnLogin.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch{
                val respond = loginUser(etLoginMail.text.toString(), etLoginPassword.text.toString())

                if(respond != null){
                    UserDataRepository.loggedUser = respond
                }

                withContext(Dispatchers.Main){
                    if(respond == null){
                        Toast.makeText(applicationContext, "Bad credentials :(", Toast.LENGTH_SHORT).show()
                    }else{
                        //Toast.makeText(applicationContext, "${UserDataRepository.loggedUser.name}", Toast.LENGTH_SHORT).show()
                        val intent = Intent(applicationContext, HomeActivity::class.java)
                        startActivity(intent)
                    }
                }
            }
        }
    }
    suspend fun loginUser(email: String, password: String): UserData?{
        val valuesToPost = HashMap<String, Any>()
        valuesToPost["Mail"] = email
        valuesToPost["Password"] = password

        val call = service.checkLogin(valuesToPost).awaitResponse()
        if(call.isSuccessful && call.body()!= null){
            return call.body()!!
        }else{
            return null
        }

    }
}
