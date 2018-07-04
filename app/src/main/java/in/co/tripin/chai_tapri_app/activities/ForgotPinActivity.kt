package `in`.co.tripin.chai_tapri_app.activities

import `in`.co.tripin.chai_tapri_app.R
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import kotlin.math.log

class ForgotPinActivity : AppCompatActivity() {

    lateinit var gotoresetpin : TextView



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_pin)
        title = "Forgot Pin"
        init()
        setListners()

    }

    private fun init() {
        gotoresetpin = findViewById(R.id.submit)

    }

    private fun setListners() {
        gotoresetpin.setOnClickListener {
            val intent = Intent(this,ResetPinActivity::class.java)
            startActivity(intent)
        }

    }
}
