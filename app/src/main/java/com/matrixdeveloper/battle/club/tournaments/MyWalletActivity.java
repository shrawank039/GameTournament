package com.matrixdeveloper.battle.club.tournaments;

import android.app.Activity;
import android.app.ProgressDialog;
import instamojo.library.InstapayListener;
import instamojo.library.InstamojoPay;

import org.json.JSONException;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.material.tabs.TabLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.braintreepayments.api.dropin.DropInActivity;
import com.braintreepayments.api.dropin.DropInRequest;
import com.braintreepayments.api.dropin.DropInResult;
import com.braintreepayments.api.interfaces.HttpResponseCallback;
import com.braintreepayments.api.internal.HttpClient;
import com.braintreepayments.api.models.PaymentMethodNonce;

import com.matrixdeveloper.battle.club.tournaments.config.JSONParser;
import com.paytm.pgsdk.PaytmOrder;
import com.paytm.pgsdk.PaytmPGService;
import com.paytm.pgsdk.PaytmPaymentTransactionCallback;

import com.matrixdeveloper.battle.club.tournaments.config.config;
import com.matrixdeveloper.battle.club.tournaments.fragment.AddMoneyFragment;
import com.matrixdeveloper.battle.club.tournaments.fragment.TransactionsFragment;
import com.matrixdeveloper.battle.club.tournaments.fragment.WithdrawFragment;
import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class MyWalletActivity extends AppCompatActivity implements PaytmPaymentTransactionCallback
   , PaymentResultListener {

    // Progress Dialog
    private ProgressDialog pDialog;

    // Creating JSON Parser object
    private final JSONParser jsonParser = new JSONParser();
    private final JSONParserString jsonParserString = new JSONParserString();

    // url to get all products list
    private static final String url = config.mainurl + "payment.php";
    private static final String urlpaytmchecksum = config.paytmchecksum + "generateChecksum.php";

    // JSON Node names
    private static final String TAG_SUCCESS = "success";

    //user
    private static final String TAG_USERID = "userid";
    private static final String TAG_USERNAME = "username";
    private static final String TAG_EMAIL = "email";
    private static final String TAG_MOBILE = "mobile";

    //balance
    private static final String TAG_USERBALANCE = "balance";

    //instamojo
    private static final String TAG_INSTA_ORDERID = "instaorderid";
    private static final String TAG_INSTA_TXNID = "instatxnid";
    private static final String TAG_INSTA_PAYMENTID = "instapaymentid";
    private static final String TAG_INSTA_TOKEN = "instatoken";

    private String balance;
    private String email;
    private LinearLayout main;
    private String number;
    private TabLayout tabLayout;
    private String username;
    private ViewPager viewPager;
    private TextView walletBalance;
    private String ammout = "";

    //Prefrance
    private static PrefManager prf;

    //paytm
    private String paytmemail;
    private String paytmphone;
    private String paytmamount;
    private String paytmpurpose;
    private String paytmbuyername;
    private String paytmorder_id;
    private String paytmchecksumhash;
    private String receipt;

    //instamojo
    InstapayListener listener;
    InstamojoPay instamojoPay;

    private String addamount;
    private String instaorderid;
    private String instatoken;
    private String instapaymentid;
    private String instatxnid;

    private int success;

    //Paypal
    final int REQUEST_CODE = 1;
    final String get_token = config.mainurl + "paypal/main.php";
    final String send_payment_details = config.mainurl + "paypal/checkout.php";
    String token, paypalamount;
    HashMap<String, String> paramHash;
    public String stringNonce;
    Checkout checkout;

    public void PaytmAddMoney(String email, String phone, String amount, String purpose, String buyername) {

        paytmemail = email;
        paytmphone = phone;
        paytmamount = amount;
        paytmpurpose = purpose;
        paytmbuyername = buyername;

        final int min = 1000;
        final int max = 10000;
        final int random = new Random().nextInt((max - min) + 1) + min;
        paytmorder_id = prf.getString(TAG_USERID) +random;

        // Join Player in Match in Background Thread
        new GetChecksum().execute();

    }

    public void callRazorPay(String email, String number, String obj, String add_money_to_wallet, String name) {

        getOrderId(obj);
    }

    private void getOrderId(final String obj) {

        JSONObject postData = new JSONObject();
        receipt = getAlphaNumericString(9);

        try {
            int a=Integer.parseInt(obj)*100;
            ammout = String.valueOf(a);
            postData.put("amount", a);
            postData.put("currency","INR");
            postData.put("receipt",receipt);
            postData.put("payment_capture",0);

        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(
                Request.Method.POST,config.getOrderID, postData,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        //progressBar.setVisibility(View.GONE);
                        if (response.optString("status").equals("success")){

                            JSONObject jsonObject;
                            try {
                                jsonObject = response.getJSONObject("data");
                                String orderID = jsonObject.optString("id");
                                callRazorPay(ammout,orderID );
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }else
                            Toast.makeText(MyWalletActivity.this, "something went wrong!!!", Toast.LENGTH_SHORT).show();
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d("error", "Error: " + error.getMessage());
                Toast.makeText(MyWalletActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                //progressBar.setVisibility(View.GONE);
            }
        }) {

            /**
             * Passing some request headers
             */
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                //headers.put("auth_token", Config.getInstance().getAuthToken());
                return headers;
            }


        };
        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(
                15000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        jsonObjReq.setShouldCache(false);
        MySingleton.getInstance(MyWalletActivity.this).addToRequestQueue(jsonObjReq);
    }

    public void callRazorPay(String amt, String orderID) {
        // Toast.makeText(this, orderID, Toast.LENGTH_SHORT).show();
        checkout.setKeyID(config.RAZOR_KEY_ID);
        /**
         * Instantiate Checkout
         */
        Checkout checkout = new Checkout();

        /**
         * Set your logo here
         */
        checkout.setImage(R.drawable.rzp_logo);

        /**
         * Reference to current activity
         */
        final Activity activity = this;
        /**
         * Pass your payment options to the Razorpay Checkout as a JSONObject
         */
        try {
            JSONObject options = new JSONObject();

            /**
             * Merchant Name
             * eg: ACME Corp || HasGeek etc.
             */
            options.put("name", prf.getString("firstname")+" "+ prf.getString("lastname"));
            /**
             * Description can be anything
             * eg: Reference No. #123123 - This order number is passed by you for your internal reference. This is not the `razorpay_order_id`.
             *     Invoice Payment
             *     etc.
             */
            options.put("description", receipt);
            options.put("image", "https://s3.amazonaws.com/rzp-mobile/images/rzp.png");
            options.put("order_id", orderID);
            options.put("currency", "INR");
            /**
             * Amount is always passed in currency subunits
             * Eg: "500" = INR 5.00
             */
            options.put("amount", amt);
            checkout.open(activity, options);

        } catch(Exception e) {
            Log.e("TAG", "Error in starting Razorpay Checkout", e);
        }
    }

    @Override
    public void onPaymentSuccess(String s) {

        String a = ammout;
        double d = Double.parseDouble(a);
        int i = (int) d / 100;

        int bal = Integer.parseInt(prf.getString(TAG_USERBALANCE)) + i;
        prf.setString(TAG_USERBALANCE, Integer.toString(bal));

        Intent intent = new Intent(MyWalletActivity.this, HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);


        Toast.makeText(MyWalletActivity.this, "Payment done. Now join match", Toast.LENGTH_LONG).show();

    }

    @Override
    public void onPaymentError(int i, String s) {
          Toast.makeText(this, "Payment Failed "+s, Toast.LENGTH_SHORT).show();
    }
    class GetChecksum extends AsyncTask<String, String, String> {
        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(MyWalletActivity.this);
            pDialog.setMessage("Loading Please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        /**
         * getting All products from url
         * */
        protected String doInBackground(String... args) {
            // Building Parameters
            Map<String, String> params = new HashMap<>();
            params.put( "MID" , config.MID);
            params.put( "ORDER_ID" , paytmorder_id);
            params.put( "CUST_ID" , prf.getString(TAG_USERID));
            params.put( "MOBILE_NO" , paytmphone);
            params.put( "EMAIL" , paytmemail);
            params.put( "CHANNEL_ID" , "WAP");
            params.put( "TXN_AMOUNT" , paytmamount);
            params.put( "WEBSITE" , config.WEBSITE);
            params.put( "INDUSTRY_TYPE_ID" , config.INDUSTRY_TYPE_ID);
            params.put( "CALLBACK_URL", config.CALLBACK_URL + paytmorder_id);

            // getting JSON string from URL
            JSONObject json = jsonParser.makeHttpRequest(urlpaytmchecksum, "POST", params);

            // Check your log cat for JSON reponse

            if(json != null){
                try {

                    paytmchecksumhash=json.has("CHECKSUMHASH")?json.getString("CHECKSUMHASH"):"";
                    Log.e("CHECKSUMHASH",paytmchecksumhash);

                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(MyWalletActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            return null;
        }

        /**
         * After completing background task Dismiss the progress dialog
         * **/
        protected void onPostExecute(String file_url) {
            // dismiss the dialog after getting all products
            pDialog.dismiss();

            // updating UI from Background Thread
            runOnUiThread(new Runnable() {
                public void run() {
                    /*
                      Updating parsed JSON data into ListView
                     */
                    try {

                        Map<String, String> paramMap = new HashMap<>();
                        paramMap.put( "MID" , config.MID);
                        // Key in your staging and production MID available in your dashboard

                        paramMap.put( "ORDER_ID" , paytmorder_id);
                        paramMap.put( "CUST_ID" , prf.getString(TAG_USERID));
                        paramMap.put( "MOBILE_NO" , paytmphone);
                        paramMap.put( "EMAIL" , paytmemail);
                        paramMap.put( "CHANNEL_ID" , "WAP");
                        paramMap.put( "TXN_AMOUNT" , paytmamount);
                        paramMap.put( "WEBSITE" , config.WEBSITE);
                        paramMap.put( "INDUSTRY_TYPE_ID" , config.INDUSTRY_TYPE_ID);
                        paramMap.put( "CALLBACK_URL", config.CALLBACK_URL + paytmorder_id);
                        paramMap.put( "CHECKSUMHASH" , paytmchecksumhash);
                        PaytmOrder Order = new PaytmOrder((HashMap<String, String>) paramMap);

                        // For Staging environment:
//                        PaytmPGService Service = PaytmPGService.getStagingService();

                        // For Production environment:
                         PaytmPGService Service = PaytmPGService.getProductionService();

                        Service.initialize(Order, null);

                        Service.startPaymentTransaction(MyWalletActivity.this, true, true, MyWalletActivity.this);

                    } catch (Exception e) {
                        System.out.println("Rjn_paytm"+e.toString());
                        e.printStackTrace();
                    }

                }
            });

        }

    }

    @Override
    public void someUIErrorOccurred(String inErrorMessage) {
        Toast.makeText(getApplicationContext(), "UI Error " + inErrorMessage, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onTransactionResponse(Bundle inResponse) {

        // getting JSON string from URL
        JSONObject json = null;
        try {
            String resstatus=inResponse.getString("STATUS");

            if(resstatus.equalsIgnoreCase("TXN_SUCCESS")) {

                instaorderid = inResponse.getString("ORDERID");
                instatxnid = inResponse.getString("TXNID");
                addamount = inResponse.getString("TXNAMOUNT");
                instapaymentid = inResponse.getString("CHECKSUMHASH");
                instatoken = inResponse.getString("MID");

                // Loading jsonarray in Background Thread
                new OneLoadAllProducts().execute();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

//        Toast.makeText(getApplicationContext(), "Payment Transaction response " + inResponse.toString(), Toast.LENGTH_LONG).show();
    }

    @Override
    public void networkNotAvailable() {
        Toast.makeText(getApplicationContext(), "Network connection error: Check your internet connectivity", Toast.LENGTH_LONG).show();
    }

    @Override
    public void clientAuthenticationFailed(String inErrorMessage) {
        Toast.makeText(getApplicationContext(), "Authentication failed: Server error" + inErrorMessage, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onErrorLoadingWebPage(int iniErrorCode, String inErrorMessage, String inFailingUrl) {
        Toast.makeText(getApplicationContext(), "Unable to load webpage " + inErrorMessage, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onBackPressedCancelTransaction() {
        Toast.makeText(getApplicationContext(), "Transaction cancelled", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onTransactionCancel(String inErrorMessage, Bundle inResponse) {
        Toast.makeText(getApplicationContext(), "Transaction Cancelled" + inResponse.toString(), Toast.LENGTH_LONG).show();
    }

    public void callInstamojoPay(String email, String phone, String amount, String purpose, String buyername) {
        final Activity activity = this;
        instamojoPay = new InstamojoPay();
        IntentFilter filter = new IntentFilter("ai.devsupport.instamojo");
        registerReceiver(instamojoPay, filter);
        JSONObject pay = new JSONObject();
        try {
            pay.put("email", email);
            pay.put("phone", phone);
            pay.put("purpose", purpose);
            addamount=amount;
            pay.put("amount", amount);
            pay.put("name", buyername);
            pay.put("send_sms", true);
            pay.put("send_email", true);
        } catch (JSONException e) {
            e.printStackTrace();
            System.out.println("instamojo_error"+e.getMessage());
        }
        initListener();
        instamojoPay.start(activity, pay, listener);
    }

    private void initListener() {
        listener = new InstapayListener() {
            @Override
            public void onSuccess(String response) {
                System.out.println("payment"+response);

                String[] str = response.split(":");
                String[] split = str[1].split("=");
                instaorderid = split[1];
                split = str[2].split("=");
                instatxnid = split[1];
                split = str[3].split("=");
                instapaymentid = split[1];
                str = str[4].split("=");
                instatoken = str[1];

                // Loading jsonarray in Background Thread
                new OneLoadAllProducts().execute();
            }

            @Override
            public void onFailure(int code, String reason) {
                System.out.println("payment_error"+"code:"+code+"reason:"+reason);
                Toast.makeText(getApplicationContext(), "Failed: " + reason, Toast.LENGTH_LONG)
                        .show();
            }
        };
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_wallet);

        checkout = new Checkout();
        checkout.setKeyID(config.RAZOR_KEY_ID);

        Checkout.preload(getApplicationContext());

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        // Call the function callInstamojo to start payment here

        prf = new PrefManager(MyWalletActivity.this);

        walletBalance = (TextView) findViewById(R.id.walletBalance);
        main = (LinearLayout) findViewById(R.id.mainLayout);
        balance = prf.getString(TAG_USERBALANCE);
        username = prf.getString(TAG_USERNAME);
        email = prf.getString(TAG_EMAIL);
        number = prf.getString(TAG_MOBILE);
        walletBalance.setText("₹ "+balance);

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        //Paypal
        if(config.paypal) {
            new HttpRequest().execute();
        }

    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new AddMoneyFragment(), "Add Money");
        adapter.addFragment(new WithdrawFragment(), "Withdraw");
        adapter.addFragment(new TransactionsFragment(), "Transactions");
        viewPager.setAdapter(adapter);
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    class OneLoadAllProducts extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(MyWalletActivity.this);
            pDialog.setMessage("Loading Please wait...");
            pDialog.setIndeterminate(true);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        /**
         * getting All products from url
         * */
        protected String doInBackground(String... args) {
            // Building Parameters
            Map<String, String> params = new HashMap<>();
            params.put(TAG_USERID, prf.getString(TAG_USERID));
            params.put("addamount", addamount);
            params.put(TAG_INSTA_ORDERID, instaorderid);
            params.put(TAG_INSTA_TXNID, instatxnid);
            params.put(TAG_INSTA_PAYMENTID, instapaymentid);
            params.put(TAG_INSTA_TOKEN, instatoken);
            params.put("status", "Add Money Success");

            // getting JSON string from URL
            JSONObject json = jsonParser.makeHttpRequest(url, "POST", params);

            // Check your log cat for JSON reponse
//            Log.d("All jsonarray: ", json.toString());

            try {
                // Checking for SUCCESS TAG
                success = json.getInt(TAG_SUCCESS);

            } catch (JSONException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        /**
         * After completing background task Dismiss the progress dialog
         * **/
        protected void onPostExecute(String file_url) {
            // dismiss the dialog after getting all products
            pDialog.dismiss();

            // updating UI from Background Thread
            runOnUiThread(new Runnable() {
                public void run() {
                    /*
                      Updating parsed JSON data into ListView
                     */
                    if (success == 1) {
                        // jsonarray found
                        // Getting Array of jsonarray
                        String s = addamount;
                        double d = Double.parseDouble(s);
                        int i = (int) d;

                        int bal = Integer.parseInt(prf.getString(TAG_USERBALANCE))+ i;
                        prf.setString(TAG_USERBALANCE,Integer.toString(bal));

                        Intent intent = new Intent(MyWalletActivity.this, HomeActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);


                        Toast.makeText(MyWalletActivity.this,"Payment done. Now join match",Toast.LENGTH_LONG).show();

                    } else {
                        Toast.makeText(MyWalletActivity.this,"Something went wrong. Try again!",Toast.LENGTH_LONG).show();

                    }

                }
            });

        }

    }

    //PayPal

    class OneLoadAllProductsPayPal extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Dialog
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (pDialog != null && pDialog.isShowing()) {
                pDialog = new ProgressDialog(MyWalletActivity.this);
                pDialog.setMessage("Loading Please wait...");
                pDialog.setIndeterminate(true);
                pDialog.setCancelable(false);
                pDialog.show();
            }
        }

        /**
         * getting All products from url
         */
        protected String doInBackground(String... args) {
            // Building Parameters
            Map<String, String> params = new HashMap<>();
            params.put(TAG_USERID, prf.getString(TAG_USERID));
            params.put("addamount", addamount);
            params.put(TAG_INSTA_ORDERID, stringNonce);
            params.put(TAG_INSTA_TXNID, "111");
            params.put(TAG_INSTA_PAYMENTID, stringNonce);
            params.put(TAG_INSTA_TOKEN, "PayPal");
            params.put("status", "Add Money Success");

            // getting JSON string from URL
            JSONObject json = jsonParser.makeHttpRequest(url, "POST", params);

            // Check your log cat for JSON reponse
//            Log.d("All jsonarray: ", json.toString());

            try {
                // Checking for SUCCESS TAG
                success = json.getInt(TAG_SUCCESS);

            } catch (JSONException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        /**
         * After completing background task Dismiss the progress dialog
         **/
        protected void onPostExecute(String file_url) {
            // dismiss the dialog after getting all products
            pDialog.dismiss();

            // updating UI from Background Thread
            runOnUiThread(new Runnable() {
                public void run() {
                /*
                  Updating parsed JSON data into ListView
                 */
                    if (success == 1) {
                        // jsonarray found
                        // Getting Array of jsonarray
                        String s = addamount;
                        double d = Double.parseDouble(s);
                        int i = (int) d;

                        int bal = Integer.parseInt(prf.getString(TAG_USERBALANCE)) + i;
                        prf.setString(TAG_USERBALANCE, Integer.toString(bal));

                        Intent intent = new Intent(MyWalletActivity.this, HomeActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);


                        Toast.makeText(MyWalletActivity.this, "Payment done. Now join match", Toast.LENGTH_LONG).show();

                    } else {
                        Toast.makeText(MyWalletActivity.this, "Something went wrong. Try again!", Toast.LENGTH_LONG).show();

                    }

                }
            });

        }

    }

    //Paypal

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                DropInResult result = data.getParcelableExtra(DropInResult.EXTRA_DROP_IN_RESULT);
                PaymentMethodNonce nonce = result.getPaymentMethodNonce();
                stringNonce = nonce.getNonce();
                System.out.println("Rajan_mylog_Result: " + stringNonce);
                // Send payment price with the nonce
                // use the result to update your UI and send the payment method nonce to your server
                if (!paypalamount.toString().isEmpty()) {
//                    amount = paypalamount.toString();
                    paramHash = new HashMap<>();
                    paramHash.put("amount", paypalamount);
                    paramHash.put("nonce", stringNonce);
//                    sendPaymentDetails();
                    // Loading jsonarray in Background Thread
                    new OneLoadAllProductsPayPalSendNonceDetails().execute();
                } else
                    Toast.makeText(MyWalletActivity.this, "Please enter a valid amount.", Toast.LENGTH_SHORT).show();

            } else if (resultCode == Activity.RESULT_CANCELED) {
                // the user canceled
                Log.d("mylog", "user canceled");
            } else {
                // handle errors here, an exception may be available in
                Exception error = (Exception) data.getSerializableExtra(DropInActivity.EXTRA_ERROR);
                System.out.println("Rajan_mylog_Error : " + error.toString());
            }
        }
    }

    public void onBraintreeSubmit(String email, String phone, String amount, String purpose, String buyername) {

        addamount = amount;
        paypalamount = amount;

        DropInRequest dropInRequest = new DropInRequest()
                .clientToken(token);
        startActivityForResult(dropInRequest.getIntent(this), REQUEST_CODE);
    }

    class OneLoadAllProductsPayPalSendNonceDetails extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Dialog
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(MyWalletActivity.this);
            pDialog.setMessage("Loading Please wait...");
            pDialog.setIndeterminate(true);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        /**
         * getting All products from url
         */
        protected String doInBackground(String... args) {
            // Building Parameters

            // getting JSON string from URL
            String json = jsonParserString.makeHttpRequest(send_payment_details, "POST", paramHash);

            // Check your log cat for JSON reponse
//            Log.d("All jsonarray: ", json.toString());

            try {
                // Checking for SUCCESS TAG
                if (json.contains("Successful")) {
                    Toast.makeText(MyWalletActivity.this, "Transaction successful", Toast.LENGTH_LONG).show();
                } else
                    Toast.makeText(MyWalletActivity.this, "Transaction failed", Toast.LENGTH_LONG).show();
                Log.d("mylog", "Final Response: " + json.toString());

            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        /**
         * After completing background task Dismiss the progress dialog
         **/
        protected void onPostExecute(String file_url) {
            // dismiss the dialog after getting all products
            pDialog.dismiss();

            // updating UI from Background Thread
            runOnUiThread(new Runnable() {
                public void run() {
                /*
                  Updating parsed JSON data into ListView
                 */
                    // Loading jsonarray in Background Thread
                    new OneLoadAllProductsPayPal().execute();


                }
            });

        }

    }

    private class HttpRequest extends AsyncTask {
        ProgressDialog progress;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progress = new ProgressDialog(MyWalletActivity.this, android.R.style.Theme_DeviceDefault_Dialog);
            progress.setCancelable(false);
            progress.setMessage("We are contacting our servers for token, Please wait");
            progress.setTitle("Getting token");
            progress.show();
        }

        @Override
        protected Object doInBackground(Object[] objects) {
            HttpClient client = new HttpClient();
            client.get(get_token, new HttpResponseCallback() {
                @Override
                public void success(String responseBody) {
                    Log.d("mylog", responseBody);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
//                            Toast.makeText(MyWalletActivity.this, "Successfully got token", Toast.LENGTH_SHORT).show();
                        }
                    });
                    token = responseBody;
                }

                @Override
                public void failure(Exception exception) {
                    final Exception ex = exception;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            System.out.println("Rajan_paypal_gettoken_failed" + ex.toString());
//                            Toast.makeText(MyWalletActivity.this, "Failed to get token: ", Toast.LENGTH_LONG).show();
                        }
                    });
                }
            });
            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            progress.dismiss();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Checkout.clearUserData(getApplicationContext());
    }

    // function to generate a random string of length n
    static String getAlphaNumericString(int n)
    {
        // chose a Character random from this String
        String AlphaNumericString = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
                + "0123456789"
                + "abcdefghijklmnopqrstuvxyz";

        // create StringBuffer size of AlphaNumericString
        StringBuilder sb = new StringBuilder(n);

        for (int i = 0; i < n; i++) {

            // generate a random number between
            // 0 to AlphaNumericString variable length
            int index
                    = (int)(AlphaNumericString.length()
                    * Math.random());

            // add Character one by one in end of sb
            sb.append(AlphaNumericString
                    .charAt(index));
        }
        return sb.toString();
    }
}
