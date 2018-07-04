package `in`.co.tripin.chai_tapri_app.activities

import `in`.co.tripin.chai_tapri_app.R
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView

class SpalshActivity : AppCompatActivity() {

    lateinit var login: TextView
    lateinit var calltoreg: TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_spalsh)
        init()
        setListners()
    }

    private fun init() {
        login = findViewById(R.id.login)
        calltoreg = findViewById(R.id.calltoreg)


    }

    private fun setListners() {

        login.setOnClickListener {
            // Go to login Screen
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)

        }

        calltoreg.setOnClickListener {
            //call to admin
        }

    }
}
