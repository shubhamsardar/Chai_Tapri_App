package `in`.co.tripin.chai_tapri_app.activities

import `in`.co.tripin.chai_tapri_app.R
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView

class LoginActivity : AppCompatActivity() {

    lateinit var forgotpin :TextView
    lateinit var login :TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        title = "Log In"
        init()
        setListners()
    }



    private fun init() {
        forgotpin = findViewById(R.id.forgotpin)
        login = findViewById(R.id.login)

    }

    private fun setListners() {
        forgotpin.setOnClickListener {
            val intent = Intent(this,ForgotPinActivity::class.java)
            startActivity(intent)
        }
        login.setOnClickListener {
            val intent = Intent(this,MainActivity::class.java)
            startActivity(intent)
        }
    }
}
