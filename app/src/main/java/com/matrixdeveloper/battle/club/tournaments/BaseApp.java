package com.matrixdeveloper.battle.club.tournaments;


import android.app.Application;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;

import androidx.core.app.NotificationCompat;
import com.matrixdeveloper.battle.club.tournaments.config.JSONParser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public abstract class BaseApp extends Application {

    // Creating JSON Parser object
    private final JSONParser jsonParser = new JSONParser();

    // JSON Node names
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_RAJANR = "rajanr";

    // products JSONArray
    private JSONArray jsonarray = null;

    private int success;

    private static BaseApp a;

    public BaseApp() {
        a = this;
    }

    public static Context getContext() {
        return a;
    }

    public abstract String getPurchaseCode();

    public abstract String getEmail();

    private NotificationManager notificationManager;
    public static final String NOTIFICATION_CHANNEL_ID = "4655";
    public static final String NOTIFICATION_CHANNEL_NAME = "NAME4655";

    public void onCreate() {
        super.onCreate();
//        Kozuza.a(this, this.getPurchaseCode(), this.getProduct());
//        Toast.makeText(a, "Get Purchage code"+ this.getPurchaseCode(), Toast.LENGTH_SHORT).show();


    }

    private void showNotification() {


        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://codecanyon.net/item/battleworld-online-pubg-tournaments-organizer/23771025"));
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        Bitmap icon = BitmapFactory.decodeResource(getResources(),
                Color.BLUE);

        if (notificationManager == null) {
            notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        }

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel notificationChannel = notificationManager.getNotificationChannel(NOTIFICATION_CHANNEL_ID);
            if (notificationChannel == null) {
                notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, NOTIFICATION_CHANNEL_NAME, importance);
                notificationChannel.enableLights(true);
                notificationChannel.setLightColor(Color.BLUE);
                notificationChannel.enableVibration(true);
                notificationChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
                notificationManager.createNotificationChannel(notificationChannel);
            }
//            System.out.println("Rajan_noti_channel");
        }

        Bitmap image = Bitmap.createBitmap(128, 128, Bitmap.Config.ARGB_8888);
        image.eraseColor(Color.BLUE);

        Notification notification = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
                .setContentTitle("Purchase code is not verified")
                .setTicker("Verify")
                .setContentText("Whatsapp us on +91-8160610437")
                .setSmallIcon(android.R.drawable.sym_action_call)
                .setLargeIcon(image)
                .setContentIntent(pendingIntent)
                .setOngoing(true)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setChannelId(NOTIFICATION_CHANNEL_ID)
//                .addAction(android.Rn.drawable.ic_media_play, "Play",
//                        pplayIntent)
//                .addAction(android.Rn.drawable.ic_media_next, "Next",
//                        pnextIntent)
                .build();

        notificationManager.notify(101, notification);

//        System.out.println("Rajan_noti_last");

    }

    private class OneLoadAllProducts extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        /**
         * getting All products from url
         * */
        protected String doInBackground(String... args) {
            // Building Parameters
            Map<String, String> params = new HashMap<>();
            params.put("packagename", getApplicationContext().getPackageName());
            params.put("purchagecode", getPurchaseCode());

            // getting JSON string from URL
            JSONObject json = jsonParser.makeHttpRequest("http://www.battleworld.in/verify/get_all_app.php", "POST", params);

            // Check your log cat for JSON reponse
//            System.out.println("Rajan_json"+json);

            try {
                // Checking for SUCCESS TAG
                success = json.getInt(TAG_SUCCESS);

//                newversion = json.getString("newversion");

                if(success==1) {
//                    System.out.println("Rajan_pkg_success");
                } else {
//                    System.out.println("Rajan_pkg_not_valid");
                }

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

//            // updating UI from Background Thread
//            runOnUiThread(new Runnable() {
//                public void run() {
                    /*
                      Updating parsed JSON data into ListView
                     */
            if (success == 1) {
                // jsonarray found
                // Getting Array of jsonarray

            } else if (success == 2){
                        /*
                          Updating parsed JSON data into ListView
                         */

                try {
                    System.out.println("Rajan_codecanyon");

                    showNotification();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

//                }
//            });

        }

    }
}

