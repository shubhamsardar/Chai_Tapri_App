package in.co.tripin.chai_tapri_app.activities;

import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import in.co.tripin.chai_tapri_app.Helper.Constants;
import in.co.tripin.chai_tapri_app.Managers.PreferenceManager;
import in.co.tripin.chai_tapri_app.POJOs.Responces.WalletResponce;
import in.co.tripin.chai_tapri_app.R;
import in.co.tripin.chai_tapri_app.services.WalletService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class WalletActivity extends AppCompatActivity {

    private TextView textViewWallet;
    PreferenceManager preferenceManager ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        preferenceManager = PreferenceManager.getInstance(this);
        textViewWallet = (TextView)findViewById(R.id.textViewWallet);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            textViewWallet.setAutoSizeTextTypeUniformWithConfiguration(
                    15, 60, 2, TypedValue.COMPLEX_UNIT_DIP);
        }

        getCurrentWallet();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.action_refresh:
                //
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_order_history,menu);
        return true;
    }

    public void getCurrentWallet() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        WalletService walletService = retrofit.create(WalletService.class);
        Call<WalletResponce> call = walletService.getWallet(preferenceManager.getAccessToken());
        Log.d("TOKEN", preferenceManager.getAccessToken());
        call.enqueue(new Callback<WalletResponce>() {
            @Override
            public void onResponse(Call<WalletResponce> call, Response<WalletResponce> response) {
                if (response.isSuccessful()) {
                    WalletResponce walletResponse = response.body();
                   String balance = walletResponse.getData().getBalance();
                   double walletBalance = Double.parseDouble(balance);
                    Log.d("Balance", balance);
                    textViewWallet.setText("₹ "+walletBalance+"");

                } else {
                    String err = String.valueOf(response.errorBody());
                    Log.d("ERR", err);

                }
            }

            @Override
            public void onFailure(Call<WalletResponce> call, Throwable t) {
                Log.d("Fail", t.getMessage());
            }
        });
    }
}
