package com.matrixdeveloper.battle.club.tournaments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import com.google.android.material.textfield.TextInputEditText;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.classes.purchaselogic.JSONParser;

import com.matrixdeveloper.battle.club.tournaments.config.config;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class WithdrawMoneyActivity extends AppCompatActivity {

    // Progress Dialog
    private ProgressDialog pDialog;

    // Creating JSON Parser object
    private final JSONParser jsonParser = new JSONParser();

    private ArrayList<HashMap<String, String>> offersList;

    // url to get all products list
    private static final String url = config.mainurl + "withdraw.php";

    // JSON Node names
    private static final String TAG_SUCCESS = "success";

    //user
    private static final String TAG_USERID = "userid";
    private static final String TAG_USERNAME = "username";
    private static final String TAG_MOBILE = "mobile";

    //balance
    private static final String TAG_USERBALANCE = "balance";


    // products JSONArray
    JSONArray jsonarray = null;

    //Prefrance
    private static PrefManager prf;

    private int success;

    //new
    private Context context;

    private String AmountToWithdraw;
    private String availableBalance;
    private TextView errorMessage;
    private String paytmNo;
    private TextView start;
    private TextInputEditText paytmNumber;
    private String username;
    private Button withdraw;
    private TextInputEditText withdrawAmount;
    private int withdrawalAmount;

    private String mode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_withdraw_money);

        context = getApplicationContext();

        mode = getIntent().getStringExtra("mode");

        prf = new PrefManager(context);

        // Hashmap for ListView
        offersList = new ArrayList<>();

        start = (TextView) findViewById(R.id.start);
        paytmNumber = (TextInputEditText) findViewById(R.id.numberWithdraw);
        withdrawAmount = (TextInputEditText) findViewById(R.id.amountWithdraw);
        withdraw = (Button) findViewById(R.id.withdrawButton);
        errorMessage = (TextView) findViewById(R.id.errorMsg);

        username = prf.getString(TAG_USERNAME);
        paytmNo = prf.getString(TAG_MOBILE);
        availableBalance = prf.getString(TAG_USERBALANCE);

        if(mode.equalsIgnoreCase("paytm")) {
            paytmNumber.setText(paytmNo);
            paytmNumber.setHint("Paytm Number");
        } else if(mode.equalsIgnoreCase("paypal")) {
//            paytmNumber.setText(paytmNo);
            start.setText("ID : ");
            paytmNumber.setHint("Enter PayPal ID");
        } else if(mode.equalsIgnoreCase("googlepay")) {
            paytmNumber.setText(paytmNo);
            paytmNumber.setHint("Google Pay Number");
        } else if(mode.equalsIgnoreCase("phonepay")) {
            paytmNumber.setText(paytmNo);
            paytmNumber.setHint("PhonePe Number");
        }

        withdraw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                paytmNo = paytmNumber.getText().toString();
                AmountToWithdraw = withdrawAmount.getText().toString().trim();
                if (!AmountToWithdraw.isEmpty()) {
                    withdrawalAmount = Integer.parseInt(AmountToWithdraw);
                    submitWithdrawData();
                    return;
                }
                errorMessage.setVisibility(View.VISIBLE);
                errorMessage.setText("Enter Withdrawal Amount");
                errorMessage.setTextColor(Color.parseColor("#ff0000"));
            }
        });

    }

    private void submitWithdrawData() {
        if(mode.equalsIgnoreCase("paypal")) {
            if(validateWithdrawalAmount()) {
                new OneLoadAllProducts().execute();
            }
        } else {
            if (validatePaytmNumber() && validateWithdrawalAmount()) {
                new OneLoadAllProducts().execute();
            }
        }
    }

    private boolean validateWithdrawalAmount() {
        if (withdrawalAmount > Integer.parseInt(availableBalance)) {
            errorMessage.setText("withdrawal amount is greater than Available balance");
            errorMessage.setTextColor(Color.parseColor("#ff0000"));
            errorMessage.setVisibility(View.VISIBLE);
            return false;
        }
        if (withdrawalAmount >= 50) {
            return true;
        }
        errorMessage.setText("Minimum withdrawal amount is ₹ 50.");
        errorMessage.setTextColor(Color.parseColor("#ff0000"));
        errorMessage.setVisibility(View.VISIBLE);
        return false;
    }

    private boolean validatePaytmNumber() {
        if (paytmNo.length() <= 10) {
            if (paytmNo.length() >= 10) {
                return true;
            }
        }
        paytmNumber.setError("Paytm Number should be of 10 Digits");
        return false;
    }

    /**
     * Prepares sample data to provide data set to adapter
     */

    class OneLoadAllProducts extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(WithdrawMoneyActivity.this);
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
            params.put(TAG_USERID, prf.getString(TAG_USERID));
            params.put("mode",mode);
            params.put("paytmnumber",paytmNo);
            params.put("withdrawamount", String.valueOf((-1)*withdrawalAmount));
            params.put("status", "Withdrawal Pending");
            // getting JSON string from URL
            JSONObject json = jsonParser.makeHttpRequest(url, "POST", params);

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
                        int bal = Integer.parseInt(prf.getString(TAG_USERBALANCE))-withdrawalAmount;
                        prf.setString(TAG_USERBALANCE,Integer.toString(bal));

                        Intent intent = new Intent(context, HomeActivity.class);
                        startActivity(intent);


                        Toast.makeText(context,"Withdrawal Request done succsessfully.",Toast.LENGTH_LONG).show();

                    } else {
                        Toast.makeText(context,"Something went wrong. Try again!",Toast.LENGTH_LONG).show();

                    }

                }
            });

        }

    }
}
