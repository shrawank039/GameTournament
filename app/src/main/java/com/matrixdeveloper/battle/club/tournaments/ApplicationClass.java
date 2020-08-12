package com.matrixdeveloper.battle.club.tournaments;

import android.util.Log;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.firebase.FirebaseApp;
import com.matrixdeveloper.battle.club.tournaments.config.MySingleton;
import com.onesignal.OSPermissionSubscriptionState;
import com.onesignal.OSSubscriptionObserver;
import com.onesignal.OSSubscriptionStateChanges;
import com.onesignal.OneSignal;
import com.matrixdeveloper.battle.club.tournaments.config.config;

import java.util.HashMap;
import java.util.Map;

public class ApplicationClass extends BaseApp implements OSSubscriptionObserver {

    private static final String TAG_EMAIL = "email";
    private static PrefManager prf;
    String onesignal_user;
    String a="";

    @Override
    public void onCreate() {
        super.onCreate();

        prf = new PrefManager(this);

        // OneSignal Initialization
        OneSignal.startInit(this)
                .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification)
                .unsubscribeWhenNotificationsAreDisabled(true)
                .init();

        OneSignal.setEmail(prf.getString(TAG_EMAIL));

        FirebaseApp.initializeApp(this);


        if (!prf.getString("userid").equals("")){
            OSPermissionSubscriptionState status = OneSignal.getPermissionSubscriptionState();
            a = status.getSubscriptionStatus().getUserId();
          //  Toast.makeText(this, a, Toast.LENGTH_SHORT).show();
            if(!a.equals("")){
            sendOnesignalID(a);}
        }
    }

    @Override
    public String getEmail() {
        return prf.getString(TAG_EMAIL);
    }

    @Override
    public void onOSSubscriptionChanged(OSSubscriptionStateChanges stateChanges) {
        if (!stateChanges.getFrom().getSubscribed() &&
                stateChanges.getTo().getSubscribed()) {
             onesignal_user = stateChanges.getTo().getUserId();
             sendOnesignalID(onesignal_user);
        }
        Log.i("onesignal_id", "onesignal_user: " + onesignal_user);
      //  Toast.makeText(this,"03 "+ onesignal_user, Toast.LENGTH_SHORT).show();
    }

    private void sendOnesignalID(final String onesignal_user) {
        String url ="https://matrixdeveloper.com/battleworld/update_onesignal.php";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("onesignal_id", response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        }){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                params.put("userid",prf.getString("userid"));
                params.put("onesignal_id",onesignal_user);
                return params;
            }
        };
        MySingleton.getInstance(getContext()).addToRequestQueue(stringRequest);
    }
}
