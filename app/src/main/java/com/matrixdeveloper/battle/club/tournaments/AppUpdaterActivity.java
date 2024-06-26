package com.matrixdeveloper.battle.club.tournaments;

import android.content.Intent;
import android.net.Uri;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.matrixdeveloper.battle.club.tournaments.config.config;

public class AppUpdaterActivity extends AppCompatActivity {

    //app
    private static final String TAG_APPNAME = "name";
    private static final String TAG_APP_OLDVERSION = "oldversion";
    private static final String TAG_APP_NEWVERSION = "newversion";

    private TextView forceUpdateNote;
    private final String isForceUpdate = "true";
    private Button later;
    private String latestVersion;
    private TextView newVersion;
    private Button update;
    private TextView updateDate;
    private String updatedOn;
    private TextView whatsNew;
    private String whatsNewData;

    private String newversion;

    public AppUpdaterActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_updater);

        newversion = getIntent().getStringExtra(TAG_APP_NEWVERSION);

        updateDate = (TextView) findViewById(R.id.date);
        newVersion = (TextView) findViewById(R.id.version);
        whatsNew = (TextView) findViewById(R.id.whatsnew);
        forceUpdateNote = (TextView) findViewById(R.id.forceUpdateNote);
        later = (Button) findViewById(R.id.laterButton);
        update = (Button) findViewById(R.id.updateButton);
        updateDate.setText(updatedOn);
        newVersion.setText("New Version: v"+newversion);
        whatsNew.setText(whatsNewData);
        if (isForceUpdate.equals("true")) {
            later.setVisibility(View.GONE);
            forceUpdateNote.setVisibility(View.VISIBLE);
        }
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(config.apkupdateurl));
                startActivity(browserIntent);
            }
        });
    }
}
