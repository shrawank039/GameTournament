package com.matrixdeveloper.battle.club.tournaments;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.annotation.NonNull;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.Toast;

import com.matrixdeveloper.battle.club.tournaments.config.JSONParser;
import com.matrixdeveloper.battle.club.tournaments.config.config;

import com.matrixdeveloper.battle.club.tournaments.fragment.BlankFragment;
import com.matrixdeveloper.battle.club.tournaments.fragment.OngoingFragment;
import com.matrixdeveloper.battle.club.tournaments.fragment.ResultFragment;
import com.matrixdeveloper.battle.club.tournaments.fragment.PlayFragment;
import com.matrixdeveloper.battle.club.tournaments.fragment.ProfileFragment;
import com.matrixdeveloper.battle.club.tournaments.fragment.EarnFragment;
import com.matrixdeveloper.battle.club.tournaments.helper.BottomNavigationBehavior;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class HomeActivity extends AppCompatActivity {

    // Creating JSON Parser object
    private final JSONParser jsonParser = new JSONParser();

    private ArrayList<HashMap<String, String>> offersList;

    // url to get all products list
    private static final String url = config.mainurl + "app_updater.php";

    // JSON Node names
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_RAJANR = "rajanr";

    //user
    private static final String TAG_USERID = "userid";

    //app
    private static final String TAG_APPNAME = "name";
    private static final String TAG_APP_OLDVERSION = "oldversion";
    private static final String TAG_APP_NEWVERSION = "newversion";

    // products JSONArray
    private JSONArray jsonarray = null;

    //Prefrance
    private static PrefManager prf;

    //back
    private static int backbackexit=1;

    private int success;
    private String newversion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        prf = new PrefManager(HomeActivity.this);

        // Hashmap for ListView
        offersList = new ArrayList<>();

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        // attaching bottom sheet behaviour - hide / show on scroll
        CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams) navigation.getLayoutParams();
        layoutParams.setBehavior(new BottomNavigationBehavior());

        //off shift mode
//        navigation.setLabelVisibilityMode(LabelVisibilityMode.LABEL_VISIBILITY_LABELED);

        loadFragment(new BlankFragment());
        navigation.setSelectedItemId(R.id.navigation_play);

        new OneLoadAllProducts().execute();

    }

    private final BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment fragment;
            switch (item.getItemId()) {
                case R.id.navigation_earn:
                    fragment = new EarnFragment();
                    loadFragment(fragment);
                    return true;
                case R.id.navigation_ongoing:
                    fragment = new OngoingFragment();
                    loadFragment(fragment);
                    return true;
                case R.id.navigation_play:
                    fragment = new BlankFragment();
                    loadFragment(fragment);
                    return true;
                case R.id.navigation_result:
                    fragment = new ResultFragment();
                    loadFragment(fragment);
                    return true;
                case R.id.navigation_profile:
                    fragment = new ProfileFragment();
                    loadFragment(fragment);
                    return true;
            }

            return false;
        }
    };

    /**
     * loading fragment into FrameLayout
     *
     * @param fragment
     */
    private void loadFragment(Fragment fragment) {
        // load fragment
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public void onBackPressed() {
        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.navigation);
        int seletedItemId = bottomNavigationView.getSelectedItemId();
        if (R.id.navigation_play != seletedItemId) {
            setHomeItem(HomeActivity.this);
        } else {
            if (backbackexit >= 2) {

                    // Creating alert Dialog with three Buttons

                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(
                            HomeActivity.this);

                    // Setting Dialog Title
                    alertDialog.setTitle(getResources().getString(R.string.app_name));

                    // Setting Dialog Message
                    alertDialog.setMessage("Are you sure you want to exit??");

                    // Setting Icon to Dialog
                    alertDialog.setIcon(R.drawable.icon);

                    // Setting Positive Yes Button
                    alertDialog.setPositiveButton("YES",
                            new DialogInterface.OnClickListener() {

                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    finish();
                                }
                            });
                    // Setting Positive Yes Button
                    alertDialog.setNeutralButton("NO",
                            new DialogInterface.OnClickListener() {

                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                }
                            });
                    // Showing Alert Message
                    alertDialog.show();
//					super.onBackPressed();
            } else {
                backbackexit++;
                Toast.makeText(getBaseContext(), "Press again to Exit", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private static void setHomeItem(Activity activity) {
        BottomNavigationView bottomNavigationView = (BottomNavigationView)
                activity.findViewById(R.id.navigation);
        bottomNavigationView.setSelectedItemId(R.id.navigation_play);
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
            params.put(TAG_USERID, prf.getString(TAG_USERID));

            // getting JSON string from URL
            JSONObject json = jsonParser.makeHttpRequest(url, "POST", params);

            // Check your log cat for JSON reponse

            try {
                // Checking for SUCCESS TAG
                success = json.getInt(TAG_SUCCESS);

//                newversion = json.getString("newversion");

                if(success==1) {
                    // jsonarray found
                    // Getting Array of jsonarray
                    jsonarray = json.getJSONArray(TAG_RAJANR);

                    // looping through All jsonarray
                    for (int i = 0; i < jsonarray.length(); i++) {
                        JSONObject c = jsonarray.getJSONObject(i);

                        // creating new HashMap
                        HashMap<String, String> map = new HashMap<>();

                        // adding each child node to HashMap key => value
                        map.put(TAG_APPNAME, c.getString(TAG_APPNAME));
                        map.put(TAG_APP_OLDVERSION, c.getString(TAG_APP_OLDVERSION));
                        map.put(TAG_APP_NEWVERSION, c.getString(TAG_APP_NEWVERSION));

                        // adding HashList to ArrayList
                        offersList.add(map);
                    }
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

            // updating UI from Background Thread
            runOnUiThread(new Runnable() {
                public void run() {
                    /*
                      Updating parsed JSON data into ListView
                     */
                    if (success == 1) {
                        // jsonarray found
                        // Getting Array of jsonarray

                        /*
                          Updating parsed JSON data into ListView
                         */
                        for (int i = 0; i < offersList.size(); i++) {

//                            play.setTitle(offersList.get(i).get(TAG_APPNAME));
//                            play.setMatchID(offersList.get(i).get(TAG_APP_OLDVERSION));
                            newversion = offersList.get(i).get(TAG_APP_NEWVERSION);

                        }

                        try {
                            PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
                            String version = pInfo.versionName;

                            System.out.println("Rajan_version" + "old:" + version + " new:" + newversion);

                            if (Float.parseFloat(version) < Float.parseFloat(newversion)) {
                                Intent intent = new Intent(HomeActivity.this, AppUpdaterActivity.class);
                                intent.putExtra(TAG_APP_NEWVERSION, newversion);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                            }
                        } catch (PackageManager.NameNotFoundException e) {
                            e.printStackTrace();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }

                }
            });

        }

    }

}
