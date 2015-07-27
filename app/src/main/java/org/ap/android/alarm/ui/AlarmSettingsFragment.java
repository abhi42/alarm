package org.ap.android.alarm.ui;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;

import org.ap.android.alarm.R;
import org.ap.android.alarm.common.AlarmUtils;

import java.text.MessageFormat;

/**
 * Created by abhi on 18.04.15.
 */
public class AlarmSettingsFragment extends PreferenceFragment implements SharedPreferences
        .OnSharedPreferenceChangeListener {

    // use PreferenceFragment instead of PreferenceActivity as suggested by android documentation
    private static final String SNOOZE_INTERVAL_SUMMARY = "{0} minutes";
    private static final String TAG = AlarmSettingsFragment.class.getName();

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preferences);
        setSummary();
    }

    @Override
    public void onResume() {
        super.onResume();
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }

    private void setSummary() {
        setSnoozeIntervalSummary();
    }

    private void setSnoozeIntervalSummary() {
        final SharedPreferences sharedPreferences = getPreferenceScreen()
                .getSharedPreferences();

        final String snoozeIntervalKey = AlarmUtils.getOrCreateSnoozeIntervalKey(getActivity());
        final int defaultSnoozeInterval = AlarmUtils.getOrCrateDefaultSnoozeInterval(getActivity());

        final String currentSnoozeInterval = sharedPreferences.getString(snoozeIntervalKey,
                String.valueOf(defaultSnoozeInterval));
        final Preference preference = findPreference(snoozeIntervalKey);
        preference.setSummary(MessageFormat.format(SNOOZE_INTERVAL_SUMMARY,
                currentSnoozeInterval));
    }

    @Override
    public void onSharedPreferenceChanged(final SharedPreferences sharedPreferences, final String
            key) {
        final String snoozeIntervalKey = AlarmUtils.getOrCreateSnoozeIntervalKey(getActivity());
        if (key.equals(snoozeIntervalKey)) {
            handleSnoozeIntervalChanged(sharedPreferences, snoozeIntervalKey);
            return;
        }
    }

    private void handleSnoozeIntervalChanged(final SharedPreferences sharedPreferences, final String snoozeIntervalKeyLocal) {
        final int defaultSnoozeInterval = AlarmUtils.getOrCrateDefaultSnoozeInterval(getActivity());
        final String currentSnoozeInterval = sharedPreferences.getString(snoozeIntervalKeyLocal,
                String.valueOf(defaultSnoozeInterval));

        final Preference preference = findPreference(snoozeIntervalKeyLocal);
        preference.setSummary(MessageFormat.format(SNOOZE_INTERVAL_SUMMARY,
                currentSnoozeInterval));
    }
}
