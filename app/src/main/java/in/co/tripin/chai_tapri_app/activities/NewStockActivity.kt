package `in`.co.tripin.chai_tapri_app.activities

import `in`.co.tripin.chai_tapri_app.Managers.Logger
import `in`.co.tripin.chai_tapri_app.Managers.PreferenceManager
import `in`.co.tripin.chai_tapri_app.POJOs.Models.UserAddress
import `in`.co.tripin.chai_tapri_app.POJOs.Responces.HubMenuResponce
import `in`.co.tripin.chai_tapri_app.R
import `in`.co.tripin.chai_tapri_app.adapters.ItemsListAdapter
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import com.android.volley.*
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.gson.Gson
import dmax.dialog.SpotsDialog
import org.json.JSONException
import org.json.JSONObject
import java.util.ArrayList
import java.util.HashMap

class NewStockActivity : AppCompatActivity() {
    private var preferenceManager: PreferenceManager? = null

    private var tapriId: String? = ""
    private var tapriName: String? = ""
    private var address: UserAddress.Data? = null
    private var queue: RequestQueue? = null
    private var tapriMenuResponce: HubMenuResponce? = null
    private var gson: Gson? = null
    private var mTotalCost: Double? = 0.0
    private var mAvailableBalance: Double? = 0.0
    internal var mMoneyTobeAdded: Double? = 0.0


    private var mSnacksList: RecyclerView? = null
    private var mBeveragesList: RecyclerView? = null
    private var mExtrasList: RecyclerView? = null
    private var mChaihiyehList: RecyclerView? = null
    private var mAddressInclude: View? = null
    private var mAddressCancel: ImageView? = null

    private var mAddressNick: TextView? = null
    private var mAddressFull: TextView? = null
    private var mProceedToPay: TextView? = null
    private var mBalance: TextView? = null
    private var mAddMoney: TextView? = null
    private var mPaymentHeader: TextView? = null
    private var mPaymentType: RadioGroup? = null
    private var mMainScroll: ScrollView? = null
    private var mNoItems: TextView? = null


    private var mSnacksAdapter: ItemsListAdapter? = null
    private var mBeveragesAdapter: ItemsListAdapter? = null
    private var mExtrasAdapter: ItemsListAdapter? = null
    private var mChahiyehAdapter: ItemsListAdapter? = null

    //private TapriMenuResponce.Data.Item[] mSnacks,mBeverages,mExtras,mChaihiyeh;

    private var mItemsToggleHeader: LinearLayout? = null
    private var mAddresseHeader: LinearLayout? = null

    private var mChaihiyehll: LinearLayout? = null, private var mBeveragesll:LinearLayout? = null, private var mSnacksll:LinearLayout? = null, private var mExtrasll:LinearLayout? = null, private var mWalletInfo:LinearLayout? = null
    private var mItemsToggleText: TextView? = null
    private var mItemsList: LinearLayout? = null
    private var dialog: AlertDialog? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_stock)
        title = "Tapri Details"
        queue = Volley.newRequestQueue(this)
        gson = Gson()

        dialog = SpotsDialog.Builder()
                .setContext(this)
                .setCancelable(false)
                .setMessage("Fetching Menu")
                .build()

        init()
        setUpView()
        setListners()

        //set title and id from intent
        if (intent.extras != null) {
            tapriName = intent.extras!!.getString("tapri_name")
            title = tapriName
            tapriId = intent.extras!!.getString("tapri_id")
            if (tapriId!!.isEmpty()) {
                finish()
            } else {
                //call tapri items api
                hitTapriItemsListAPI()
            }
        }


    }

    private fun setListners() {
        mItemsToggleHeader!!.setOnClickListener {
            if (mItemsList!!.visibility != View.GONE) {
                mItemsList!!.visibility = View.GONE
                mItemsToggleText!!.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_arrow_drop_down_black_24dp, 0, 0, 0)
            } else {
                mItemsList!!.visibility = View.VISIBLE
                mItemsToggleText!!.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_arrow_drop_up_black_24dp, 0, 0, 0)

            }
        }

        mAddresseHeader!!.setOnClickListener { startActivityForResult(Intent(this@TapriDetailsActivity, SelectAddressActivity::class.java), 1) }

        mAddressInclude!!.setOnClickListener { startActivityForResult(Intent(this@TapriDetailsActivity, SelectAddressActivity::class.java), 1) }

        mProceedToPay!!.setOnClickListener {
            val mItems = ArrayList<TapriMenuResponce.Data.Item>()
            var cost = 0.0



            for (i in 0 until mChahiyehAdapter!!.data.length) {

                if (mChahiyehAdapter!!.data[i].getQuantity() !== 0) {

                    mItems.add(mChahiyehAdapter!!.data[i])
                    var rate: Double? = 0.0
                    try {
                        rate = java.lang.Double.parseDouble(mChahiyehAdapter!!.data[i].getRate())
                    } catch (e: NumberFormatException) {
                        Logger.v("Rate Invalid: cant convert to double")
                    }

                    cost = cost + mChahiyehAdapter!!.data[i].getQuantity() * rate!!
                }

            }
            for (i in 0 until mExtrasAdapter!!.data.length) {

                if (mExtrasAdapter!!.data[i].getQuantity() !== 0) {

                    mItems.add(mExtrasAdapter!!.data[i])
                    var rate: Double? = 0.0
                    try {
                        rate = java.lang.Double.parseDouble(mExtrasAdapter!!.data[i].getRate())
                    } catch (e: NumberFormatException) {
                        Logger.v("Rate Invalid: cant convert to double")
                    }

                    cost = cost + mExtrasAdapter!!.data[i].getQuantity() * rate!!
                }
            }
            for (i in 0 until mSnacksAdapter!!.data.length) {

                if (mSnacksAdapter!!.data[i].getQuantity() !== 0) {

                    mItems.add(mSnacksAdapter!!.data[i])
                    var rate: Double? = 0.0
                    try {
                        rate = java.lang.Double.parseDouble(mSnacksAdapter!!.data[i].getRate())
                    } catch (e: NumberFormatException) {
                        Logger.v("Rate Invalid: cant convert to double")
                    }

                    cost = cost + mSnacksAdapter!!.data[i].getQuantity() * rate!!
                }
            }
            for (i in 0 until mBeveragesAdapter!!.data.length) {

                if (mBeveragesAdapter!!.data[i].getQuantity() !== 0) {

                    mItems.add(mBeveragesAdapter!!.data[i])
                    var rate: Double? = 0.0
                    try {
                        rate = java.lang.Double.parseDouble(mBeveragesAdapter!!.data[i].getRate())
                    } catch (e: NumberFormatException) {
                        Logger.v("Rate Invalid: cant convert to double")
                    }

                    cost = cost + mBeveragesAdapter!!.data[i].getQuantity() * rate!!
                }
            }


            if (address == null) {
                Toast.makeText(applicationContext, "Address Required!", Toast.LENGTH_LONG).show()
            } else {

                var paymentMethod = ""
                if (mPaymentType!!.checkedRadioButtonId == R.id.radiocod) {
                    paymentMethod = "COD"
                } else if (mPaymentType!!.checkedRadioButtonId == R.id.radiowallet) {
                    paymentMethod = "Wallet"
                }

                val orderSummeryPOJO = OrderSummeryPOJO(tapriId,
                        tapriName,
                        java.lang.Double.toString(cost),
                        address,
                        paymentMethod,
                        mItems)

                if (orderSummeryPOJO.getmItems().size() !== 0) {
                    if (orderSummeryPOJO.getmPaymentMethod().equals("Wallet")) {
                        if (mAvailableBalance < mTotalCost) {
                            Toast.makeText(applicationContext, "Balance is Insufficient, Add Money!", Toast.LENGTH_LONG).show()
                        } else {
                            val i = Intent(this@TapriDetailsActivity, OrderSummeryActivity::class.java)
                            i.putExtra("ordersummery", orderSummeryPOJO)
                            startActivity(i)
                        }
                    } else {
                        val i = Intent(this@TapriDetailsActivity, OrderSummeryActivity::class.java)
                        i.putExtra("ordersummery", orderSummeryPOJO)
                        startActivity(i)
                    }

                } else {
                    Toast.makeText(applicationContext, "Select Some Items!", Toast.LENGTH_LONG).show()

                }

            }
        }

        mPaymentType!!.setOnCheckedChangeListener { group, checkedId ->
            if (checkedId == R.id.radiocod) {
                mWalletInfo!!.setVisibility(View.INVISIBLE)
            } else if (checkedId == R.id.radiowallet) {
                mWalletInfo!!.setVisibility(View.VISIBLE)
                FetchCurrentBalance()

            }
        }

        mAddMoney!!.setOnClickListener {
            val i = Intent(this@TapriDetailsActivity, WalletActivity::class.java)
            if (mAvailableBalance < mTotalCost) {
                val tobeadded = mTotalCost!! - mAvailableBalance!!
                i.putExtra("money", "" + tobeadded)
            }
            startActivity(i)
        }
    }


    private fun init() {
        Logger.v("setting UI..")

        mBeveragesList = findViewById(R.id.beverages)
        mSnacksList = findViewById(R.id.snacks)
        mExtrasList = findViewById(R.id.extras)
        mChaihiyehList = findViewById(R.id.chaihiyeh)

        mAddressNick = findViewById(R.id.addressnick)
        mAddressNick!!.text = "Your Default Address"
        mAddressFull = findViewById(R.id.fulladdress)


        mBeveragesll = findViewById(R.id.beveragesll)
        mSnacksll = findViewById(R.id.snacksll)
        mChaihiyehll = findViewById(R.id.chaihiyehll)
        mExtrasll = findViewById(R.id.extrasll)

        mItemsToggleHeader = findViewById(R.id.itrms_header)
        mAddresseHeader = findViewById(R.id.address_header)

        mItemsList = findViewById(R.id.items_lists)
        mItemsToggleText = findViewById(R.id.items_title)
        mAddressInclude = findViewById(R.id.address_include)
        mAddressCancel = findViewById(R.id.remove)

        mProceedToPay = findViewById(R.id.proceedtopay)
        mPaymentType = findViewById(R.id.payment_type)
        mBalance = findViewById(R.id.balance)
        mAddMoney = findViewById(R.id.addmoney)
        mPaymentHeader = findViewById(R.id.payment_title)
        mNoItems = findViewById(R.id.tv_noitems)

        mWalletInfo = findViewById(R.id.llwalletinfo)
        mMainScroll = findViewById(R.id.mainscroll)


    }

    private fun setUpView() {

        //        mSnacks = new TapriMenuResponce.Data.Item[]{};
        //        mBeverages = new TapriMenuResponce.Data.Item[]{};
        //        mExtras = new TapriMenuResponce.Data.Item[]{};


        val mLayoutManager = LinearLayoutManager(applicationContext)
        mSnacksList!!.layoutManager = mLayoutManager
        val mLayoutManager1 = LinearLayoutManager(applicationContext)
        mBeveragesList!!.layoutManager = mLayoutManager1
        val mLayoutManager2 = LinearLayoutManager(applicationContext)
        mExtrasList!!.layoutManager = mLayoutManager2
        val mLayoutManager3 = LinearLayoutManager(applicationContext)
        mChaihiyehList!!.layoutManager = mLayoutManager3

        preferenceManager = PreferenceManager.getInstance(this)

        mAddressCancel!!.visibility = View.GONE
        if (preferenceManager!!.getDefaultAddress() == null) {
            mAddressInclude!!.visibility = View.GONE
        } else {
            mAddressInclude!!.visibility = View.VISIBLE
            address = gson!!.fromJson(preferenceManager!!.getDefaultAddress(), UserAddress.Data::class.java)
            mAddressFull!!.setText(address!!.getFullAddressString())

        }

    }

    private fun hitTapriItemsListAPI() {

        Logger.v("getting menu...")
        dialog!!.show()
        val url = "http://139.59.70.142:3055/api/v1/tapri/$tapriId/items"

        val getRequest = object : JsonObjectRequest(Request.Method.GET, url, null,
                Response.Listener { response ->
                    // display response
                    //Toast.makeText(getApplicationContext(), "List Fetched!", Toast.LENGTH_SHORT).show();
                    Logger.v("Response: " + response.toString())
                    tapriMenuResponce = Gson().fromJson<TapriMenuResponce>(response.toString(), TapriMenuResponce::class.java!!)
                    if (tapriMenuResponce != null) {
                        setItems(tapriMenuResponce!!.getData())
                    }
                },
                Response.ErrorListener { error ->
                    Logger.v("Error.Response: " + error.toString())
                    Toast.makeText(applicationContext, "Server Error", Toast.LENGTH_SHORT).show()
                    dialog!!.dismiss()
                }
        ) {
            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                val params = HashMap<String, String>()
                params["Content-Type"] = "application/json"
                params["token"] = preferenceManager!!.getAccessToken()
                return params
            }


            override fun getBodyContentType(): String {
                return "application/json; charset=utf-8"
            }


        }
        queue!!.add(getRequest)
    }

    private fun setItems(data: TapriMenuResponce.Data) {

        setListVisiblity(data)


        mBeveragesAdapter = ItemsListAdapter(this, data.getBeverages(), object : ItemSelectionCallback() {
            fun onitemAdded(cost: Double?, quant: Int) {
                mTotalCost = mTotalCost!! + cost!! * quant
                updateTotalCostUI()
            }

            fun onItemRemoved(cost: Double?, quant: Int) {
                mTotalCost = mTotalCost!! - cost!! * quant
                updateTotalCostUI()

            }
        })
        mExtrasAdapter = ItemsListAdapter(this, data.getExtra(), object : ItemSelectionCallback() {
            fun onitemAdded(cost: Double?, quant: Int) {
                mTotalCost = mTotalCost!! + cost!! * quant
                updateTotalCostUI()

            }

            fun onItemRemoved(cost: Double?, quant: Int) {
                mTotalCost = mTotalCost!! - cost!! * quant
                updateTotalCostUI()

            }
        })
        mSnacksAdapter = ItemsListAdapter(this, data.getSnacks(), object : ItemSelectionCallback() {
            fun onitemAdded(cost: Double?, quant: Int) {
                mTotalCost = mTotalCost!! + cost!! * quant
                updateTotalCostUI()

            }

            fun onItemRemoved(cost: Double?, quant: Int) {
                mTotalCost = mTotalCost!! - cost!! * quant
                updateTotalCostUI()

            }
        })
        mChahiyehAdapter = ItemsListAdapter(this, data.getChaihiyeh(), object : ItemSelectionCallback() {
            fun onitemAdded(cost: Double?, quant: Int) {
                mTotalCost = mTotalCost!! + cost!! * quant
                updateTotalCostUI()

            }

            fun onItemRemoved(cost: Double?, quant: Int) {
                mTotalCost = mTotalCost!! - cost!! * quant
                updateTotalCostUI()

            }
        })

        //Logger.v("in Extras: "+mExtras[0].toString());

        mBeveragesList!!.adapter = mBeveragesAdapter
        mSnacksList!!.adapter = mSnacksAdapter
        mExtrasList!!.adapter = mExtrasAdapter
        mChaihiyehList!!.adapter = mChahiyehAdapter


        //        mSnacksAdapter.notifyDataSetChanged();
        //        mBeveragesAdapter.notifyDataSetChanged();
        //        mExtrasAdapter.notifyDataSetChanged();


        dialog!!.dismiss()


    }

    private fun updateTotalCostUI() {
        mPaymentHeader!!.text = "Payment : ₹" + mTotalCost!!
        if (mTotalCost > mAvailableBalance) {
            mAddMoney!!.background = ContextCompat.getDrawable(applicationContext, R.drawable.button_light_selector)
            mMoneyTobeAdded = mTotalCost!! - mAvailableBalance!!
            mAddMoney!!.text = "Add ₹" + (mTotalCost!! - mAvailableBalance!!)
        } else {
            mAddMoney!!.background = ContextCompat.getDrawable(applicationContext, R.drawable.background_white)
            mMoneyTobeAdded = 0.0
            mAddMoney!!.text = "Add Money!"
        }
    }

    private fun setListVisiblity(data: HubMenuResponce.Data) {

        mBeveragesll!!.setVisibility(View.VISIBLE)
        mChaihiyehll!!.visibility = View.VISIBLE
        mSnacksll!!.setVisibility(View.VISIBLE)
        mExtrasll!!.setVisibility(View.VISIBLE)
        var nocheck = false
        if (data.getBeverages().length === 0) {
            mBeveragesll!!.setVisibility(View.GONE)
            nocheck = true
        }
        if (data.getChaihiyeh().length === 0) {
            mChaihiyehll!!.visibility = View.GONE
            nocheck = true
        }
        if (data.getExtra().length === 0) {
            mExtrasll!!.setVisibility(View.GONE)
            nocheck = true
        }
        if (data.getSnacks().length === 0) {
            mSnacksll!!.setVisibility(View.GONE)
            nocheck = true
        }
        if (!nocheck) {
            mNoItems!!.visibility = View.GONE
        }
    }

    // Call Back method  to get the Message form other Activity
    override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent) {
        super.onActivityResult(requestCode, resultCode, intent)

        Logger.v("On Activity Result : result code: $resultCode request code:$requestCode")

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 1) {
                Toast.makeText(applicationContext, "Address Selected!", Toast.LENGTH_SHORT).show()

                address = intent.getSerializableExtra("address") as UserAddress.Data
                val fulladdressstring = (address!!.getLandmark() + ", "
                        + address!!.getFlatSociety() + ", "
                        + address!!.getAddressLine1() + ", "
                        + address!!.getAddressLine2() + ", "
                        + address!!.getCity() + ", "
                        + address!!.getCountry())

                mAddressFull!!.setText(fulladdressstring)
                mAddressNick!!.setText(address!!.getNickname())
                preferenceManager!!.setDefaultAddress(gson!!.toJson(address))
                mAddressInclude!!.visibility = View.VISIBLE
                mAddressInclude!!.background = ContextCompat.getDrawable(applicationContext, R.color.colorHighlight)
                Logger.v("selected address is: $fulladdressstring")
            }
        } else {
            Toast.makeText(applicationContext, "Address not selected!", Toast.LENGTH_SHORT).show()
        }


    }


}
