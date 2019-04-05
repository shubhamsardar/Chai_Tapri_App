package in.co.tripin.chai_tapri_app.activities;

import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import in.co.tripin.chai_tapri_app.Helper.Constants;
import in.co.tripin.chai_tapri_app.Helper.DateFormatHelper;
import in.co.tripin.chai_tapri_app.Managers.PreferenceManager;
import in.co.tripin.chai_tapri_app.POJOs.Responces.TransactionsResponce;
import in.co.tripin.chai_tapri_app.POJOs.Responces.WalletResponce;
import in.co.tripin.chai_tapri_app.R;
import in.co.tripin.chai_tapri_app.services.WalletService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class WalletActivity extends AppCompatActivity {

    private TextView textViewWallet,tvUserName;
    PreferenceManager preferenceManager ;
    CustomAdapter customAdapter;
    ArrayList<TransactionsResponce.Data> transactionList;
    ListView transactionListView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        preferenceManager = PreferenceManager.getInstance(this);
        textViewWallet = (TextView)findViewById(R.id.textViewWallet);
        tvUserName = (TextView)findViewById(R.id.tvUserName);
        transactionListView = (ListView) findViewById(R.id.transactionList);



        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            textViewWallet.setAutoSizeTextTypeUniformWithConfiguration(
                    15, 60, 2, TypedValue.COMPLEX_UNIT_DIP);
        }

        tvUserName.setText(preferenceManager.getUserName());




        getCurrentWallet();
        fetchTransactions();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.action_refresh:
                getCurrentWallet();
                fetchTransactions();
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

    public void fetchTransactions()
    {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        WalletService  walletService = retrofit.create(WalletService.class);
        Call<TransactionsResponce> call = walletService.getTransactions(preferenceManager.getAccessToken());
        call.enqueue(new Callback<TransactionsResponce>() {
            @Override
            public void onResponse(Call<TransactionsResponce> call, retrofit2.Response<TransactionsResponce> response) {
                if(response.isSuccessful())
                {
                    TransactionsResponce transactionsResponce = response.body();
                    transactionList = (ArrayList<TransactionsResponce.Data>) transactionsResponce.getData();

                    customAdapter = new CustomAdapter(WalletActivity.this, android.R.layout.simple_list_item_1, transactionList);
                    transactionListView.setAdapter(customAdapter);

                }
            }

            @Override
            public void onFailure(Call<TransactionsResponce> call, Throwable t) {

            }
        });

    }

    public class  CustomAdapter extends ArrayAdapter<TransactionsResponce.Data> {

        Context context;
        ArrayList<TransactionsResponce.Data> transactionList;
        View view;

        public CustomAdapter(@NonNull Context context, int resource, @NonNull ArrayList<TransactionsResponce.Data> transactionList) {
            super(context, resource, transactionList);
            this.context = context;
            this.transactionList = transactionList;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            view = getLayoutInflater().inflate(R.layout.custom_transactions, null);

            TextView tvAmount = (TextView) view.findViewById(R.id.tvAmount);
            TextView tvDate = (TextView) view.findViewById(R.id.tvDate);
            TextView tvType = (TextView) view.findViewById(R.id.tvType);

            tvAmount.setText("₹ " + transactionList.get(position).getAmount());

            tvDate.setText(DateFormatHelper.getDisplayableDate(transactionList.get(position).getCreatedAt()));
            tvType.setText(transactionList.get(position).getType());


            return view;
        }
    }
}
