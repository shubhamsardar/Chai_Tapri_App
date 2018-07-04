package `in`.co.tripin.chai_tapri_app.activities

import `in`.co.tripin.chai_tapri_app.R
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView

class ManageItemsActivity : AppCompatActivity() {

    lateinit var manageItems : TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manage_items)
        title = "Manage Items"
        init()
        setListners()
    }

    private fun init() {
        manageItems = findViewById(R.id.managelist)
    }


    private fun setListners() {
        manageItems.setOnClickListener {
            val intent = Intent(this,ManageItemsListActivity::class.java)
            startActivity(intent)
        }
    }

}
