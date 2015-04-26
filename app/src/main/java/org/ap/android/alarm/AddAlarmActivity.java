package org.ap.android.alarm;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Date;


public class AddAlarmActivity extends BaseAlarmActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setAlarmStatus(true); // for a new alarm, let it be enabled by default
    }

    public void onSubmitAlarmClick(final View view) {
        handleCreateAlarm();
    }

    private void handleCreateAlarm() {
        registerAlarmInDb();
    }

    private void createAlarm(final long alarmId) {
        AlarmUtils.createInitialAlarm(this, alarmId, getSelectedDateTime(), getSelectedInterval(),
                getSelectedNumOccurrences(),
                getSelectedAlarmDesc());
    }

    private void registerAlarmInDb() {
        final AlarmDbHelper alarmDbHelper = getOrCreateDbHelper();
        final AlarmDto dto = createAlarmDtoFromSelection();
        new RegisterAlarmInDbTask(this, alarmDbHelper).execute(dto);
    }

    @Override
    protected void getInitialUiValues() {
        populateUi(new AlarmDto("", getDisplayDateForNewAlarm(), 1, 60, true));
    }

    private Date getDisplayDateForNewAlarm() {
        final Calendar c = Calendar.getInstance();

        final int hr = c.get(Calendar.HOUR_OF_DAY);
        final int hrToBeShown = hr + 1;
        c.set(Calendar.HOUR_OF_DAY, hrToBeShown);

        return c.getTime();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_add_alarm;
    }

    protected void afterOperationPerformed(final long alarmId) {
        if (isSelectedAlarmStatusEqualToEnabled()) {
            createAlarm(alarmId);
        }
        Toast.makeText(this, "Alarm added ", Toast.LENGTH_LONG).show();
        finish();
    }
}
