package in.co.tripin.chai_tapri_app.networking;

import in.co.tripin.chai_tapri_app.POJOs.Bodies.LogInBody;
import in.co.tripin.chai_tapri_app.POJOs.Responces.LogInResponce;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface APIInterface {


    @POST("/user/signIn")
    Call<LogInResponce> logInUser(@Body LogInBody body);

}
