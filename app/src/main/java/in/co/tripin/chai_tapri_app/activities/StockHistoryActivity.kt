package `in`.co.tripin.chai_tapri_app.activities

import `in`.co.tripin.chai_tapri_app.R
import android.support.v7.app.AppCompatActivity
import android.os.Bundle

class StockHistoryActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stock_history)
        title = "Stock History"
    }
}
