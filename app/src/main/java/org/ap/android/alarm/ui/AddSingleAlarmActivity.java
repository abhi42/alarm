package org.ap.android.alarm.ui;

import android.widget.Toast;

import org.ap.android.alarm.R;
import org.ap.android.alarm.common.AlarmUtils;
import org.ap.android.alarm.db.AlarmDbHelper;
import org.ap.android.alarm.db.RegisterAlarmInDbTask;
import org.ap.android.alarm.dto.AlarmDto;

/**
 * Activity that is composed of modular fragments
 * Created by abhi on 17.05.15.
 */
public class AddSingleAlarmActivity extends SingleAlarmActivity {

    @Override
    protected int getLayoutId() {
        return R.layout.activity_single_alarm;
    }

    @Override
    protected void handleAlarmOperation() {
        registerAlarmInDb(AlarmDto.getCurrentDto());
    }

    @Override
    protected void getValuesToPopulateUi() {
        AlarmDto.setCurrentDto(null);
    }

    private void registerAlarmInDb(final AlarmDto dto) {
        final AlarmDbHelper alarmDbHelper = getOrCreateDbHelper();
        new RegisterAlarmInDbTask(this, alarmDbHelper).execute(dto);
    }

    @Override
    public void onInsertOperationPerformed(final long alarmId) {
        final AlarmDto dto = AlarmDto.getCurrentDto();
        dto.setId(alarmId);
        if (dto.isEnabled()) {
            createAlarm();
        }
        Toast.makeText(this, "Alarm added ", Toast.LENGTH_LONG).show();
        finish();
    }

    private void createAlarm() {
        AlarmUtils.createAlarmInSystem(this, AlarmDto.getCurrentDto());
    }
}
