package com.matrixdeveloper.battle.club.tournaments;

import com.classes.purchaselogic.BaseApplication;
import com.google.firebase.FirebaseApp;
import com.onesignal.OneSignal;
import com.matrixdeveloper.battle.club.tournaments.config.config;

public class ApplicationClass extends BaseApplication {

    private static final String TAG_EMAIL = "email";

    //Prefrance
    private static PrefManager prf;

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
    }

    @Override
    public String getPurchaseCode() {
        return config.PURCHASE_CODE;
    }

    @Override
    public String getEmail() {
        return prf.getString(TAG_EMAIL);
    }
}
