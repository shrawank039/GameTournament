package com.matrixdeveloper.battle.club.tournaments;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.JsonObject;
import com.matrixdeveloper.battle.club.tournaments.config.config;
import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class RazorPay extends AppCompatActivity implements PaymentResultListener {

    Checkout checkout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_razor_pay);

        checkout = new Checkout();
       // checkout.setKeyID(config.RAZOR_KEY_ID);
        Checkout.preload(getApplicationContext());
    }

    public void pay(View view) {

        getOrderId();
    }

    private void getOrderId() {

            JSONObject postData = new JSONObject();

            try {
                postData.put("amount", 500);
                postData.put("currency","INR");
                postData.put("receipt","order_rcptid_11");
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
                                    callRazorPay(orderID);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                            }else
                                Toast.makeText(RazorPay.this, "something went wrong!!!", Toast.LENGTH_SHORT).show();
                        }
                    }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    VolleyLog.d("error", "Error: " + error.getMessage());
                    Toast.makeText(RazorPay.this, error.getMessage(), Toast.LENGTH_SHORT).show();
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
            MySingleton.getInstance(RazorPay.this).addToRequestQueue(jsonObjReq);
        }

    public void callRazorPay(String orderID) {
        Toast.makeText(this, orderID, Toast.LENGTH_SHORT).show();

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
            options.put("name", "Merchant Name");

            /**
             * Description can be anything
             * eg: Reference No. #123123 - This order number is passed by you for your internal reference. This is not the `razorpay_order_id`.
             *     Invoice Payment
             *     etc.
             */
            options.put("description", "Reference No. #123456");
            options.put("image", "https://s3.amazonaws.com/rzp-mobile/images/rzp.png");
            options.put("order_id", orderID);
            options.put("currency", "INR");
            /**
             * Amount is always passed in currency subunits
             * Eg: "500" = INR 5.00
             */
            options.put("amount", "500");
            checkout.open(activity, options);

        } catch(Exception e) {
            Log.e("TAG", "Error in starting Razorpay Checkout", e);
        }
    }

    @Override
    public void onPaymentSuccess(String s) {
        Toast.makeText(this, "Payment Success "+s, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPaymentError(int i, String s) {
      //  Toast.makeText(this, "Payment Failed "+s, Toast.LENGTH_SHORT).show();
    }
}
