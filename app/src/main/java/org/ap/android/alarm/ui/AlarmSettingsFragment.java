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
    private static final String RING_DURATION_SUMMARY = "{0} seconds";

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preferences);
        setPreferenceSummary();
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

    private void setPreferenceSummary() {
        setSnoozeIntervalSummary();
        setRingDurationSummary();
    }

    private void setRingDurationSummary() {
        final String ringDurationKey = AlarmUtils.getOrCreateRingDurationKey(getActivity());
        final int defaultRingDuration = AlarmUtils.getOrCreateDefaultRingDuration(getActivity());
        setPreferenceSummary(ringDurationKey, defaultRingDuration, RING_DURATION_SUMMARY);
    }

    private void setPreferenceSummary(final String key, final int defaultValue, final String messageTemplate) {
        final SharedPreferences sharedPreferences = getPreferenceScreen()
                .getSharedPreferences();
        final String currentValue = sharedPreferences.getString(key, String.valueOf(defaultValue));
        final Preference preference = findPreference(key);
        preference.setSummary(MessageFormat.format(messageTemplate, currentValue));
    }

    private void setSnoozeIntervalSummary() {
        final String snoozeIntervalKey = AlarmUtils.getOrCreateSnoozeIntervalKey(getActivity());
        final int defaultSnoozeInterval = AlarmUtils.getOrCreateDefaultSnoozeInterval(getActivity());
        setPreferenceSummary(snoozeIntervalKey, defaultSnoozeInterval, SNOOZE_INTERVAL_SUMMARY);
    }

    @Override
    public void onSharedPreferenceChanged(final SharedPreferences sharedPreferences, final String
            key) {
        final String snoozeIntervalKey = AlarmUtils.getOrCreateSnoozeIntervalKey(getActivity());
        if (key.equals(snoozeIntervalKey)) {
            handleSnoozeIntervalChanged(sharedPreferences, snoozeIntervalKey);
            return;
        }
        final String ringDurationKey = AlarmUtils.getOrCreateRingDurationKey(getActivity());
        if (key.equals(ringDurationKey)) {
            handleRingDurationChanged(sharedPreferences, ringDurationKey);
        }
    }

    private void handleRingDurationChanged(final SharedPreferences sharedPreferences, final String ringDurationKey) {
        final int defaultRingDuration = AlarmUtils.getOrCreateDefaultRingDuration(getActivity());
        handlePreferenceValueChanged(sharedPreferences, ringDurationKey, defaultRingDuration, RING_DURATION_SUMMARY);
    }

    private void handlePreferenceValueChanged(final SharedPreferences sharedPreferences, final String key, final int defaultValue, final String messageTemplate) {
        final String currentValue = sharedPreferences.getString(key, String.valueOf(defaultValue));
        final Preference preference = findPreference(key);
        preference.setSummary(MessageFormat.format(messageTemplate, currentValue));
    }

    private void handleSnoozeIntervalChanged(final SharedPreferences sharedPreferences, final String snoozeIntervalKeyLocal) {
        final int defaultSnoozeInterval = AlarmUtils.getOrCreateDefaultSnoozeInterval(getActivity());
        handlePreferenceValueChanged(sharedPreferences, snoozeIntervalKeyLocal, defaultSnoozeInterval, SNOOZE_INTERVAL_SUMMARY);
    }
}
