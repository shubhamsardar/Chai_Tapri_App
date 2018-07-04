package `in`.co.tripin.chai_tapri_app.activities

import `in`.co.tripin.chai_tapri_app.R
import android.support.v7.app.AppCompatActivity
import android.os.Bundle

class ManageItemsListActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manage_items_list)
        title = "Manage Items List"

    }
}
