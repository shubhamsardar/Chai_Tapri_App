package `in`.co.tripin.chai_tapri_app.networking

import `in`.co.tripin.chai_tapri_app.POJOs.Bodies.ForgotPassOTPBody
import `in`.co.tripin.chai_tapri_app.POJOs.Bodies.LogInBody
import `in`.co.tripin.chai_tapri_app.POJOs.Responces.ForgotPassResponce
import `in`.co.tripin.chai_tapri_app.POJOs.Responces.LogInResponce
import io.reactivex.Observable
import retrofit2.Call
import retrofit2.Response
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.Retrofit
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query


public interface APIService {

//
//    @GET("search/users")
//    fun search(@Query("q") query: String,
//               @Query("page") page: Int,
//               @Query("per_page") perPage: Int): Observable<Result>

    @POST("user/signIn")
    abstract fun logInUser(@Body body: LogInBody): Observable<Response<LogInResponce>>

    @POST("otp/send")
    abstract fun sendOTPforgotpassword(@Body body: ForgotPassOTPBody): Observable<Response<ForgotPassResponce>>

    /**
     * Companion object to create the GithubApiService
     */
    companion object Factory {
        fun create(): APIService {
            val retrofit = Retrofit.Builder()
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .baseUrl("http://139.59.70.142:3055/api/v1/")
                    .build()

            return retrofit.create(APIService::class.java)
        }
    }
}
