package org.ap.android.alarm.ui;


import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;

import org.ap.android.alarm.R;

public class AlarmSettingsActivity extends ActionBarActivity {

    private static final String TAG = AlarmSettingsActivity.class.getName();

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_settings);

        // using android.R.id.content does not work, hence using a dummy layout file R.id.settingsTopLevelView
        getFragmentManager().beginTransaction().replace(android.R.id.content, new
                AlarmSettingsFragment()).commit();
        Log.d(TAG, "Replaced contents with those from preferences xml");
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
    }
}
