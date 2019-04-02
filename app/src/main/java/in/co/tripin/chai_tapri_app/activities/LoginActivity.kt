package `in`.co.tripin.chai_tapri_app.activities

import `in`.co.tripin.chai_tapri_app.Helper.Constants
import `in`.co.tripin.chai_tapri_app.Managers.Logger
import `in`.co.tripin.chai_tapri_app.Managers.PreferenceManager
import `in`.co.tripin.chai_tapri_app.POJOs.Bodies.LogInBody
import `in`.co.tripin.chai_tapri_app.POJOs.Responces.LogInResponce
import `in`.co.tripin.chai_tapri_app.POJOs.Responces.MappedHubResponce
import `in`.co.tripin.chai_tapri_app.POJOs.Responces.TapriStatusResponce
import `in`.co.tripin.chai_tapri_app.POJOs.Responces.TapriTypePojo
import `in`.co.tripin.chai_tapri_app.R
import `in`.co.tripin.chai_tapri_app.networking.APIClient
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import `in`.co.tripin.chai_tapri_app.networking.APIInterface
import `in`.co.tripin.chai_tapri_app.networking.APIService
import com.basgeekball.awesomevalidation.AwesomeValidation
import com.basgeekball.awesomevalidation.ValidationStyle
import com.basgeekball.awesomevalidation.utility.RegexTemplate
import android.R.string.cancel
import android.R.attr.name
import android.R.attr.data
import android.app.AlertDialog
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.gson.Gson
import com.keiferstone.nonet.NoNet
import dmax.dialog.SpotsDialog
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_new_stock.*
import kotlinx.android.synthetic.main.content_main.*
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Response
import java.util.HashMap
import javax.security.auth.callback.Callback


class LoginActivity : AppCompatActivity() {

    lateinit var forgotpin: TextView
    lateinit var login: TextView
    lateinit var mobile: EditText
    lateinit var pin: EditText
    private var dialog: AlertDialog? = null
    private var queue: RequestQueue? = null
    lateinit var apiService: APIService



    lateinit var awesomeValidation: AwesomeValidation
    lateinit var logInBody: LogInBody
    lateinit var apiSetvice: APIService
    private var mCompositeDisposable: CompositeDisposable? = null
    lateinit var preferenceManager: PreferenceManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        mCompositeDisposable = CompositeDisposable()
        apiSetvice = APIService.create()
        preferenceManager = PreferenceManager.getInstance(this)
        queue = Volley.newRequestQueue(this)
        apiService = APIService.create()

        dialog = SpotsDialog.Builder()
                .setContext(this)
                .setCancelable(false)
                .setMessage("Loading")
                .build()

        title = "Log In"
        init()
        setListners()
        internetCheck()
    }


    private fun init() {
        forgotpin = findViewById(R.id.forgotpin)
        login = findViewById(R.id.login)
        mobile = findViewById(R.id.mobile)
        pin = findViewById(R.id.pin)

        awesomeValidation = AwesomeValidation(ValidationStyle.BASIC)
        addValidations()

    }


    private fun setListners() {
        forgotpin.setOnClickListener {
            val intent = Intent(this, ForgotPinActivity::class.java)
            startActivity(intent)
        }
        login.setOnClickListener {

            dialog!!.show()

            if (awesomeValidation.validate()) {
                logInBody = LogInBody()
                logInBody.mobile = mobile.text.toString().trim()
                logInBody.pin = pin.text.toString().trim()
                logInBody.regToken = preferenceManager.fcmId
                Log.v("OnLogin: ",logInBody.regToken)
                mCompositeDisposable?.add( apiSetvice.logInUser(logInBody)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe(this::handleResponse, this::handleError))

            }

        }
    }

    private fun addValidations() {

        awesomeValidation.addValidation(this, R.id.mobile, RegexTemplate.NOT_EMPTY, R.string.err_mobile)
        awesomeValidation.addValidation(this, R.id.pin, RegexTemplate.NOT_EMPTY, R.string.error_invalid_password)
        val regexMobile = ".{10,}"
        awesomeValidation.addValidation(this, R.id.mobile, regexMobile, R.string.invalid_mobile)
        val regexPassword = ".{4,}"
        awesomeValidation.addValidation(this, R.id.pin, regexPassword, R.string.error_invalid_password)


    }

    private fun handleResponse(responce: Response<LogInResponce>) {

        dialog!!.dismiss()
        Log.v("OnResponceLogin",responce.toString())

        val loginResponce : LogInResponce? = responce.body()
        if (loginResponce != null) {
            if(loginResponce.data.status == "Logged In"){

                //save data to shared preferences

                var isRole = false
                for(role: LogInResponce.Data.Role in loginResponce.data.roles){
                    if(role.roleType == "10016"){
                        isRole = true
                        preferenceManager.tapriId = role.id
                    }
                }

                if(isRole){
                    preferenceManager.accessToken = responce.headers().get("token")
                    Log.v("token: ",preferenceManager.accessToken)
                    preferenceManager.mobileNo = loginResponce.data.mobile
                    preferenceManager.userName = loginResponce.data.name
                    getTapriType()
                    fetchAssignedHubDetails()
                }else{
                    Toast.makeText(applicationContext,"Not A User! Register First",Toast.LENGTH_LONG).show()
                    val intent = Intent(this,SpalshActivity::class.java)
                    startActivity(intent)
                    finish()
                }

            }else{
                Log.v("OnResponceLogin","failed Logged In")
                Toast.makeText(applicationContext,"Login Failed!",Toast.LENGTH_LONG).show()
            }
        }else{
            Log.v("OnResponceLogin","null")
            Toast.makeText(applicationContext,"Responce Null",Toast.LENGTH_LONG).show()


        }


    }

    private fun handleError(error: Throwable) {

        dialog!!.dismiss()
        Log.v("OnErrorLogin",error.toString())
        Toast.makeText(applicationContext,"Server Error!",Toast.LENGTH_LONG).show()

    }


    override fun onDestroy() {
        super.onDestroy()
        mCompositeDisposable?.clear()
    }

    private fun getTapriType() {

        Logger.v("Getting Tapri Type")
        dialog!!.show()
        val url = Constants.BASE_URL + "api/v1/tapri/fixed"
        val getRequest = object : JsonObjectRequest(Request.Method.GET, url, null,
                com.android.volley.Response.Listener<JSONObject> { response ->
                    // display response
                    dialog!!.dismiss()
                    Logger.v("ResponseStatus :" + response.toString())
                    val tapriStatusResponce: TapriTypePojo = Gson().fromJson(response.toString(), TapriTypePojo::class.java)
                    if (tapriStatusResponce.data.isFixed == 1) {
                        preferenceManager.tapriType = "1"
                    } else {
                        preferenceManager.tapriType = "0"
                    }

                    Toast.makeText(applicationContext, "Login Success!", Toast.LENGTH_SHORT).show()

                    Logger.v("Tapri Type :" + preferenceManager.tapriType)

                    val intent = Intent(this,MainActivity::class.java)
                    startActivity(intent)
                    finishAffinity()

                },
                com.android.volley.Response.ErrorListener { error ->
                    dialog!!.dismiss()
                    Logger.d("Error.Response: " + error.toString())
                    preferenceManager.accessToken = null
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

    private fun internetCheck() {
        NoNet.monitor(this)
                .poll()
                .snackbar()
    }

    private fun fetchAssignedHubDetails() {
        mCompositeDisposable?.add(apiService.getHubDetails(preferenceManager.accessToken)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponse, this::handleErrors))

    }

    private fun handleResponse(responce: MappedHubResponce) {

        if(responce.status == "Success"){
            Toast.makeText(applicationContext,"Connected to Hub",Toast.LENGTH_SHORT).show()
            Log.v("OnResponceMappedTapri: ",responce.status)
            preferenceManager.setHubId(responce.data.hubId)
            preferenceManager.setHubName(responce.data.name)

        }else{
            Toast.makeText(applicationContext,"Error!",Toast.LENGTH_SHORT).show()
        }




    }

    private fun handleErrors(error: Throwable) {
        Log.v("OnErrorMappedTapri",error.toString())
        Toast.makeText(applicationContext,"Server Error!",Toast.LENGTH_SHORT).show()

    }


}
