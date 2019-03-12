package in.co.tripin.chai_tapri_app.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.google.zxing.Result;

import java.io.IOException;
import java.io.StringReader;

import in.co.tripin.chai_tapri_app.Helper.Constants;
import in.co.tripin.chai_tapri_app.Managers.PreferenceManager;
import in.co.tripin.chai_tapri_app.Model.QRRequestBody;
import in.co.tripin.chai_tapri_app.R;
import in.co.tripin.chai_tapri_app.services.QrCodeService;
import me.dm7.barcodescanner.zxing.ZXingScannerView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.Manifest.permission.CAMERA;

public class QRCodeScannerActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler {


    private static final int REQEST_CAMERA = 1;
    private ZXingScannerView scannerView;
    private final Context context = this;
    PreferenceManager preferenceManager;
    String qrCode,orderId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FrameLayout contentFrameLayout = (FrameLayout) findViewById(R.id.contentFrame); //Remember this is the FrameLayout area within your activity_main.xml
        setContentView(getLayoutInflater().inflate(R.layout.activity_qrcode_scanner, contentFrameLayout));
preferenceManager = PreferenceManager.getInstance(this);
        scannerView = new ZXingScannerView(this);
        setContentView(scannerView);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkPermission()) {

            } else {
                requestPermission();
            }
        }

        Intent intent = getIntent();
        orderId = intent.getStringExtra("ORDERID");

    }
    @Override
    public void onResume() {
        super.onResume();
        scannerView.setResultHandler(this);
        scannerView.startCamera();          // Start camera on resume
    }

    @Override
    public void onPause() {
        super.onPause();
        scannerView.stopCamera();           // Stop camera on pause
    }

    private boolean checkPermission() {
        return (ContextCompat.checkSelfPermission(QRCodeScannerActivity.this, CAMERA) == PackageManager.PERMISSION_GRANTED);
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{CAMERA}, REQEST_CAMERA);
    }

    @Override
    public void handleResult(Result rawResult) {
//        Toast.makeText(this, "Contents = " + rawResult.getText() +
//                ", Format = " + rawResult.getBarcodeFormat().toString(), Toast.LENGTH_SHORT).show();
        qrCode = rawResult.getText();
        placeOrder();
        //startActivity(new Intent(QRCodeScannerActivity.this,HomeActivity.class));

        // Note:
        // * Wait 2 seconds to resume the preview.
        // * On older devices continuously stopping and resuming camera preview can result in freezing the app.
        // * I don't know why this is the case but I don't have the time to figure out.
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                scannerView.resumeCameraPreview(QRCodeScannerActivity.this);
            }
        }, 2000);

    }
    public  void  placeOrder()
    {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        QrCodeService qrCodeService = retrofit.create(QrCodeService.class);
        Call<QRRequestBody> call = qrCodeService.toOrder(preferenceManager.getAccessToken(),new QRRequestBody(qrCode),orderId);
        call.enqueue(new Callback<QRRequestBody>() {
            @Override
            public void onResponse(Call<QRRequestBody> call, Response<QRRequestBody> response) {
                if(response.isSuccessful()) {

                    AlertDialog alertDialog = new AlertDialog.Builder(context)
                            .setCancelable(false)
                            .setTitle("Success")
                            .setMessage("Payment received successfully")
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    finish();
                                    dialog.dismiss();
                                }
                            })
                            .create();

                    alertDialog.show();

                    //Toast.makeText(QRCodeScannerActivity.this, "Ooooooo", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    try {
                        Log.d("ERR",response.errorBody().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<QRRequestBody> call, Throwable t) {

            }
        });


    }
}
