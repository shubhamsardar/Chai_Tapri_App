package in.co.tripin.chai_tapri_app.services;

import in.co.tripin.chai_tapri_app.POJOs.Responces.OrderHistoryResponce;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;

public interface CreditService {

    @GET("api/v1/creditOrders")
    Call<OrderHistoryResponce> getCreditOrders(@Header("token")String token);
}
