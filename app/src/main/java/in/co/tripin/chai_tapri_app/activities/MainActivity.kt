package `in`.co.tripin.chai_tapri_app.activities

import `in`.co.tripin.chai_tapri_app.Helper.Constants
import `in`.co.tripin.chai_tapri_app.Managers.Logger
import `in`.co.tripin.chai_tapri_app.Managers.PreferenceManager
import `in`.co.tripin.chai_tapri_app.POJOs.Responces.PendingOrdersResponce
import `in`.co.tripin.chai_tapri_app.POJOs.Responces.TapriStatusResponce
import `in`.co.tripin.chai_tapri_app.R
import `in`.co.tripin.chai_tapri_app.adapters.PendingAdapter
import `in`.co.tripin.chai_tapri_app.adapters.PendingOrdersInteractionCallback
import `in`.co.tripin.chai_tapri_app.networking.APIService
import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.VolleyLog
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.gson.Gson
import com.keiferstone.nonet.NoNet
import dmax.dialog.SpotsDialog
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.content_main.*
import org.json.JSONException
import org.json.JSONObject
import java.io.UnsupportedEncodingException
import java.util.*

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener, PendingOrdersInteractionCallback {


    lateinit var switch: Switch
    private var dialog: AlertDialog? = null
    private var isActive = false

    lateinit var mContext: Context
    lateinit var apiService: APIService
    private var mCompositeDisposable: CompositeDisposable? = null
    lateinit var preferenceManager: PreferenceManager
    private var queue: RequestQueue? = null
    lateinit var linearLayoutManager: LinearLayoutManager
    lateinit var pinMenuItem: MenuItem
    lateinit var gson: Gson
    lateinit var tapriname: TextView

    var isLocationUpdated = false

    private var mFusedLocationClient: FusedLocationProviderClient? = null


    private val REQUIRED_SDK_PERMISSIONS = arrayOf(Manifest.permission.INTERNET,
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION)

    internal val REQUEST_CODE_ASK_PERMISSIONS = 1002


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        mContext = this
        gson = Gson()
        mCompositeDisposable = CompositeDisposable()
        apiService = APIService.create()
        preferenceManager = PreferenceManager.getInstance(this)
        queue = Volley.newRequestQueue(this)
        linearLayoutManager = LinearLayoutManager(this)
        linearLayoutManager.reverseLayout = true
        linearLayoutManager.stackFromEnd = true
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        pendinglist.layoutManager = linearLayoutManager
        dialog = SpotsDialog.Builder()
                .setContext(this)
                .setCancelable(false)
                .setMessage("Loading")
                .build()
        title = "Fetching..."
        val toggle = ActionBarDrawerToggle(
                this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()
        nav_view.setNavigationItemSelectedListener(this)
        fab.setOnClickListener {
            fetchPendingOrders()
        }


        if (preferenceManager.userName != null) {
            tapriname = nav_view.getHeaderView(0).findViewById(R.id.tapriname)
            tapriname.text = preferenceManager.userName.toUpperCase()
        }

        internetCheck()


    }

    override fun onStart() {
        super.onStart()
        fetchPendingOrders()
    }

    private fun fetchPendingOrders() {

        Logger.v(">> Fetching Pending Orders")
        title = "Fetching..."
        dialog!!.show()
        mCompositeDisposable?.add(apiService.getPendingOrders(preferenceManager.accessToken)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponse, this::handleError))

    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            finish()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        pinMenuItem = menu.findItem(R.id.action_settings)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> {
                if (isActive) {
                    toggleTapriAvailablity("inactive")
                } else {
                    toggleTapriAvailablity("active")
                }
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        when (item.itemId) {
            R.id.nav_pending -> {
                // Handle the camera action
            }
            R.id.nav_history -> {
                val intent = Intent(this, OrderHistoryActivity::class.java)
                startActivity(intent)
            }
            R.id.nav_stocknew -> {
                val intent = Intent(this, NewStockActivity::class.java)
                startActivity(intent)
            }
            R.id.nav_stockhistory -> {
                val intent = Intent(this, StockHistoryActivity::class.java)
                startActivity(intent)
            }
            R.id.nav_manage -> {
                val intent = Intent(this, ManageItemsActivity::class.java)
                startActivity(intent)
            }

            R.id.nav_changepin -> {
                val intent = Intent(this, ChangePinActivity::class.java)
                startActivity(intent)
            }

            R.id.nav_logout -> {
                preferenceManager.clearLoginPreferences()
                val intent = Intent(this, SpalshActivity::class.java)
                startActivity(intent)
                finish()
            }

            R.id.nav_rate -> {
                rateApp()
            }
        }

        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

    private fun handleResponse(responce: PendingOrdersResponce) {

        Logger.v(">> GOT Pending Orders")
        Log.v("OnResponcePending: ", responce.status)
        pendinglist.adapter = PendingAdapter(responce.data, this, this)
        val size: Int = responce.data.size
        when (size) {
            0 -> {
                title = "No Orders for you"
            }
            1 -> {
                title = "$size Order Pending"
            }
            else -> {
                title = "$size Orders Pending"
            }
        }
        getTapriAvailablity()

    }

    private fun handleError(error: Throwable) {
        Log.v("OnErrorPending", error.toString())
    }


    override fun onDestroy() {
        super.onDestroy()
        mCompositeDisposable?.clear()
    }

    override fun onOrderAccepted(mOrderId: String?) {
        callEditOrderAPI(mOrderId, "accepted")

    }

    override fun onOrderRejected(mOrderId: String?) {
        callEditOrderAPI(mOrderId, "rejected")
    }

    override fun onOrderSent(mOrderId: String?) {
        callEditOrderAPI(mOrderId, "sent")
    }

    @SuppressLint("MissingPermission")
    override fun onCalledCustomer(mMobile: String?) {
        //call to admin
        if (isPermissionGranted()) {
            call_action(mMobile)
        }
    }

    private fun callEditOrderAPI(mOrderId: String?, mOperation: String) {

        Logger.v("Marking Order Recived")
        dialog!!.show()
        val url = Constants.BASE_URL + "api/v2/order/$mOrderId/status/$mOperation"
        val getRequest = object : JsonObjectRequest(Request.Method.GET, url, null,
                com.android.volley.Response.Listener<JSONObject> { response ->
                    // display response
                    dialog!!.dismiss()
                    Logger.v("ResponseEdit :" + response.toString())
                    Toast.makeText(this, mOperation, Toast.LENGTH_LONG).show()
                    fetchPendingOrders()

                },
                com.android.volley.Response.ErrorListener { error ->
                    dialog!!.dismiss()
                    Logger.d("Error.Response: " + error.toString())
                    Toast.makeText(applicationContext, "Server Error", Toast.LENGTH_SHORT).show()
                }
        ) {

            override fun getHeaders(): MutableMap<String, String> {
                val params = HashMap<String, String>()
                params["token"] = preferenceManager.accessToken
                return params
            }
        }
        queue!!.add(getRequest)
    }

    private fun toggleTapriAvailablity(mOperation: String) {

        Logger.v("Toggeling Availablity")
        dialog!!.show()
        val url = Constants.BASE_URL + "api/v1/tapri/$mOperation"
        val getRequest = object : JsonObjectRequest(Request.Method.PATCH, url, null,
                com.android.volley.Response.Listener<JSONObject> { response ->
                    // display response
                    dialog!!.dismiss()
                    Logger.v("ResponseEdit :" + response.toString())
                    getTapriAvailablity()
                    Toast.makeText(this, "Tapri ${mOperation.toUpperCase()}", Toast.LENGTH_LONG).show()
                },
                com.android.volley.Response.ErrorListener { error ->
                    dialog!!.dismiss()
                    Logger.d("Error.Response: " + error.toString())
                    Toast.makeText(applicationContext, "Server Error", Toast.LENGTH_SHORT).show()
                }
        ) {

            override fun getHeaders(): MutableMap<String, String> {
                val params = HashMap<String, String>()
                params["token"] = preferenceManager.accessToken
                return params
            }
        }
        queue!!.add(getRequest)
    }

    private fun getTapriAvailablity() {

        Logger.v(">>Getting Tapri Availablity")
        dialog!!.show()
        val url = Constants.BASE_URL + "api/v1/tapri/status"
        val getRequest = object : JsonObjectRequest(Request.Method.GET, url, null,
                com.android.volley.Response.Listener<JSONObject> { response ->
                    // display response
                    dialog!!.dismiss()
                    Logger.v(">>Got Tapri Availablity")
                    Logger.v("ResponseStatus :" + response.toString())
                    val tapriStatusResponce: TapriStatusResponce = gson.fromJson(response.toString(), TapriStatusResponce::class.java)
                    if (tapriStatusResponce.data.flag == 1) {
                        pinMenuItem.setIcon(R.drawable.ic_togglebn_avail)
                        isActive = true
                        supportActionBar!!.subtitle = "Active for new orders"
                        inactivewarning.visibility = View.GONE

                    } else {
                        pinMenuItem.setIcon(R.drawable.ic_togglebtn_unavail)
                        isActive = false
                        supportActionBar!!.subtitle = "Inactive for new orders"
                        inactivewarning.visibility = View.VISIBLE
                    }

                    if (!isLocationUpdated) {
                        if (preferenceManager.tapriType.equals("0")) {
                            GetCurrentLocation()
                        }
                    }

                },
                com.android.volley.Response.ErrorListener { error ->
                    dialog!!.dismiss()
                    Logger.d("Error.Response: " + error.toString())
                    Toast.makeText(applicationContext, "Server Error", Toast.LENGTH_SHORT).show()
                    if (!isLocationUpdated) {
                        if (preferenceManager.tapriType.equals("0")) {
                            GetCurrentLocation()
                        }
                    }
                }
        ) {

            override fun getHeaders(): MutableMap<String, String> {
                val params = HashMap<String, String>()
                params["token"] = preferenceManager.accessToken
                return params
            }
        }
        queue!!.add(getRequest)
    }

    fun isPermissionGranted(): Boolean {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(android.Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                Log.v("TAG", "Permission is granted")
                return true
            } else {

                Log.v("TAG", "Permission is revoked")
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CALL_PHONE), 1)
                return false
            }
        } else { //permission is automatically granted on sdk<23 upon installation
            Log.v("TAG", "Permission is granted")
            return true
        }
    }


    @SuppressLint("MissingPermission")
    fun call_action(mMobile: String?) {
        Logger.v("Mobile : $mMobile")
        val callIntent = Intent(Intent.ACTION_CALL)
        callIntent.data = Uri.parse("tel:$mMobile")
        startActivity(callIntent)
    }


    internal fun rateApp() {
        val uri = Uri.parse("market://details?id=" + mContext.packageName)
        val goToMarket = Intent(Intent.ACTION_VIEW, uri)
        // To count with Play market backstack, After pressing back button,
        // to taken back to our application, we need to add following flags to intent.
        goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY or
                Intent.FLAG_ACTIVITY_NEW_DOCUMENT or
                Intent.FLAG_ACTIVITY_MULTIPLE_TASK)
        try {
            startActivity(goToMarket)
        } catch (e: ActivityNotFoundException) {
            startActivity(Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://play.google.com/store/apps/details?id=" + mContext.packageName)))
        }

    }

    protected fun checkPermissions() {
        Log.d("checkPermissions", "Inside")
        val missingPermissions = ArrayList<String>()
        // check all required dynamic permissions
        for (permission in REQUIRED_SDK_PERMISSIONS) {
            val result = ContextCompat.checkSelfPermission(this, permission)
            if (result != PackageManager.PERMISSION_GRANTED) {
                missingPermissions.add(permission)
            }
        }
        if (!missingPermissions.isEmpty()) {
            Log.d("checkPermissions", "missingPermissions is not empty")
            // request all missing permissions
            val permissions = missingPermissions
                    .toTypedArray()
            ActivityCompat.requestPermissions(this, permissions,
                    REQUEST_CODE_ASK_PERMISSIONS)
        } else {
            //            Logger.v(" premissions already granted ");
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>,
                                            grantResults: IntArray) {
        Log.d("onRequestPermissions", "Inside")
        when (requestCode) {
            REQUEST_CODE_ASK_PERMISSIONS -> for (index in permissions.indices.reversed()) {
                if (grantResults[index] != PackageManager.PERMISSION_GRANTED) {
                    // exit the app if one permission is not granted
                    Toast.makeText(this, "Required permission '" + permissions[index]
                            + "' not granted, exiting!", Toast.LENGTH_LONG).show()
                    return
                } else {
                    //granted: update location
                    if (!isLocationUpdated) {
                        if (preferenceManager.tapriType.equals("0")) {
                            GetCurrentLocation()
                        }
                    }
                }
            }
        }
    }

    private fun GetCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(applicationContext, "Permissions Needed!", Toast.LENGTH_LONG).show()
            checkPermissions()
        } else {
            Logger.v(">>Getting Current Location")
            mFusedLocationClient!!.lastLocation
                    .addOnSuccessListener(this) { location ->
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            Logger.v(">>Geot Current Location")
                            Toast.makeText(applicationContext, "Updating new tapri location!", Toast.LENGTH_LONG).show()
                            prepareUpdateLocationApi(location)
                        } else {
                            Toast.makeText(applicationContext, "Error! Set location form menu!", Toast.LENGTH_LONG).show()
                        }
                    }
        }

    }

    private fun prepareUpdateLocationApi(location: Location) {

        val jsonObject = JSONObject()
        try {

            val jsonObject1 = JSONObject()
            jsonObject1.put("latitude", location.latitude)
            jsonObject1.put("longitude", location.longitude)
            jsonObject.put("location", jsonObject1)
            Logger.v("Body :" + jsonObject.toString())
            hitUpdateLocationApi(jsonObject)

        } catch (e: JSONException) {
            e.printStackTrace()
            Logger.v("Request Body Parsing Error")
        }


    }

    private fun hitUpdateLocationApi(jsonObject: JSONObject) {
        Logger.v(">>Hitting update location API")
        dialog!!.show()
        val url = Constants.BASE_URL + "api/v1/tapri/location"
        val getRequest = object : JsonObjectRequest(Request.Method.PATCH, url, null,
                com.android.volley.Response.Listener<JSONObject> { response ->
                    // display response
                    dialog!!.dismiss()
                    Logger.v(">>Location Update ResponseStatus :" + response.toString())
                    Toast.makeText(applicationContext, "Location Updated!", Toast.LENGTH_SHORT).show()
                    isLocationUpdated = true
                },
                com.android.volley.Response.ErrorListener { error ->
                    dialog!!.dismiss()
                    Logger.d("Error.Response: " + error.toString())
                    Toast.makeText(applicationContext, "Server Error", Toast.LENGTH_SHORT).show()
                }
        ) {

            override fun getHeaders(): MutableMap<String, String> {
                val params = HashMap<String, String>()
                params["token"] = preferenceManager.accessToken
                return params
            }

            override fun getBodyContentType(): String {
                return "application/json; charset=utf-8"
            }

            override fun getBody(): ByteArray? {
                try {
                    return if (jsonObject.toString() == null) null else jsonObject.toString().toByteArray(charset("utf-8"))
                } catch (uee: UnsupportedEncodingException) {
                    VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", jsonObject.toString(), "utf-8")
                    return null
                }

            }
        }
        queue!!.add(getRequest)
    }

    /**
     * This method is use for checking internet connectivity
     * If there is no internet it will show an snackbar to user
     */
    private fun internetCheck() {
        NoNet.monitor(this)
                .poll()
                .snackbar()
    }


}
