package in.co.tripin.chai_tapri_app.services;

import in.co.tripin.chai_tapri_app.POJOs.Responces.WalletResponce;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;

public interface WalletService {


    @GET("/api/v2/users/wallet/balance")
    Call<WalletResponce> getWallet(@Header("token") String token);
}
