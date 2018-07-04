package `in`.co.tripin.chai_tapri_app.activities

import `in`.co.tripin.chai_tapri_app.R
import android.support.v7.app.AppCompatActivity
import android.os.Bundle

class OrderHistoryActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_history)
        title = "Order History"
    }
}
