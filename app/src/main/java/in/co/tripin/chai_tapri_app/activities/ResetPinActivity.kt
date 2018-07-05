package `in`.co.tripin.chai_tapri_app.activities

import `in`.co.tripin.chai_tapri_app.POJOs.Bodies.ForgotPassOTPBody
import `in`.co.tripin.chai_tapri_app.POJOs.Responces.ForgotPassResponce
import `in`.co.tripin.chai_tapri_app.POJOs.Responces.LogInResponce
import `in`.co.tripin.chai_tapri_app.R
import `in`.co.tripin.chai_tapri_app.networking.APIService
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_login.*
import retrofit2.Response

class ResetPinActivity : AppCompatActivity() {

    lateinit var submit:TextView
    lateinit var otpguide:TextView
    lateinit var resend:TextView


    private var mCompositeDisposable: CompositeDisposable? = null
    lateinit var apiSetvice: APIService
    var mobile: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reset_pin)
        mCompositeDisposable = CompositeDisposable()
        apiSetvice = APIService.create()

        title = "Reset Pin"
        init()
        setListners()
        mobile = intent.extras.getString("mobile")

        if(mobile != null){
            sendOTP()
        }else{
            finish()
        }

    }

    private fun sendOTP() {
        val body = ForgotPassOTPBody()
        body.mobile = mobile
        body.type = "forget_password_otp"
        mCompositeDisposable?.add(apiSetvice.sendOTPforgotpassword(body)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponse, this::handleError))
    }


    private fun init() {
        submit = findViewById(R.id.submit)
        otpguide = findViewById(R.id.otpguide)
        resend = findViewById(R.id.resend)


    }

    private fun setListners() {
        submit.setOnClickListener {


            val intent = Intent(this,LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

        resend.setOnClickListener{
            sendOTP()
        }
    }

    private fun handleResponse(responce: Response<ForgotPassResponce>) {

        Log.v("OnResponceOTPSent",responce.toString())
        otpguide.setText("an OTP is sent to $mobile")


    }

    private fun handleError(error: Throwable) {
        Log.v("OnErrorOTP",error.toString())
        otpguide.setText("Error Occured! Please Resend")

    }

    override fun onDestroy() {
        super.onDestroy()
        mCompositeDisposable?.clear()
    }


}
