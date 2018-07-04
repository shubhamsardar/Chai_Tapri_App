package `in`.co.tripin.chai_tapri_app.activities

import `in`.co.tripin.chai_tapri_app.R
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView

class ResetPinActivity : AppCompatActivity() {

    lateinit var submit:TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reset_pin)
        title = "Reset Pin"
        init()
        setListners()

    }



    private fun init() {
        submit = findViewById(R.id.submit)
    }

    private fun setListners() {
        submit.setOnClickListener {
            val intent = Intent(this,LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }


}
