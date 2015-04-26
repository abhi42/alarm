package org.ap.android.alarm;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

/**
 * Created by abhi on 01.03.15.
 */
public class UpdateAlarmActivity extends BaseAlarmActivity {

    private AlarmDbHelper dbHelper;

    private static final String[] COLUMNS_TO_BE_RETRIEVED = {
            AlarmContract.AlarmEntry._ID,
            AlarmContract.AlarmEntry.COLUMN_NAME_ALARM_DESC,
            AlarmContract.AlarmEntry.COLUMN_NAME_ALARM_START_TIME,
            AlarmContract.AlarmEntry.COLUMN_NAME_ALARM_TIME_ZONE,
            AlarmContract.AlarmEntry.COLUMN_NAME_ALARM_NUM_OCCURRENCES,
            AlarmContract.AlarmEntry.COLUMN_NAME_ALARM_INTERVAL,
            AlarmContract.AlarmEntry.COLUMN_NAME_ALARM_IS_ENABLED
    };

    private static final String TAG = UpdateAlarmActivity.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        dbHelper = new AlarmDbHelper(this);
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void getInitialUiValues() {
        new RetrieveAlarmTask(this, dbHelper, COLUMNS_TO_BE_RETRIEVED).execute(getAlarmId());
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_add_alarm;
    }

    public void onSubmitAlarmClick(final View view) {
        updateAlarmInDb();
    }

    private void updateAlarmInDb() {
        if (isSelectedAlarmStatusEqualToEnabled()) {
            registerOrUpdateAlarmWithSystem();
        } else {
            cancelAlarmInSystem();
        }
        final AlarmDbHelper alarmDbHelper = getOrCreateDbHelper();
        final AlarmDto dto = createAlarmDtoFromSelection();
        new UpdateAlarmInDbTask(this, alarmDbHelper).execute(dto);
    }

    private void registerOrUpdateAlarmWithSystem() {
        Log.d(TAG, "About to update alarm in system");
        AlarmUtils.createAlarm(this, getAlarmId(), getSelectedDateTime(), getSelectedInterval(), getSelectedNumOccurrences(),
                getSelectedAlarmDesc());
    }
}
