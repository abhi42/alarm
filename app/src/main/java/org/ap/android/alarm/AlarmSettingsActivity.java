package org.ap.android.alarm;


import android.app.Activity;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.util.Log;

public class AlarmSettingsActivity extends Activity {

    private static final String TAG = AlarmSettingsActivity.class.getName();

    @Override
    public void onCreate(final Bundle savedInstanceState, final PersistableBundle persistentState) {
        Log.d(TAG, "in onCreate");
        super.onCreate(savedInstanceState, persistentState);

        getFragmentManager().beginTransaction().replace(android.R.id.content, new
                SettingsFragment()).commit();
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
    }

    // use PreferenceFragment instead of PreferenceActivity as suggested by android documentation
    public static class SettingsFragment extends PreferenceFragment {

        @Override
        public void onCreate(final Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            // Load the preferences from an XML resource
            addPreferencesFromResource(R.xml.preferences);
            Log.d(TAG, "added preference from resource in settings fragment");
            getActivity().getActionBar().setHomeButtonEnabled(true);
        }
    }
}
