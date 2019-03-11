package in.co.tripin.chai_tapri_app.services;

import in.co.tripin.chai_tapri_app.Model.QRRequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface QrCodeService {

    @POST("/api/v2/wodOrder/orderId")
    Call<QRRequestBody> toOrder(@Header("token")String token,@Body QRRequestBody qrRequestBody);
}
