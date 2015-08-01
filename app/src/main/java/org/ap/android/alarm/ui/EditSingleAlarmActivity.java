package org.ap.android.alarm.ui;

import org.ap.android.alarm.R;
import org.ap.android.alarm.common.AlarmUtils;
import org.ap.android.alarm.db.AlarmContract;
import org.ap.android.alarm.db.AlarmDbHelper;
import org.ap.android.alarm.db.UpdateAlarmInDbTask;
import org.ap.android.alarm.dto.AlarmDto;
import org.ap.android.alarm.task.RetrieveAlarmTask;

/**
 * Created by abhi on 14.06.15.
 */
public class EditSingleAlarmActivity extends SingleAlarmActivity {

    @Override
    protected int getLayoutId() {
        return R.layout.activity_single_alarm;
    }

    @Override
    protected void handleAlarmOperation() {
        final AlarmDto dto = AlarmDto.getCurrentDto();
        updateAlarmInDb(dto);
    }

    @Override
    protected void getValuesToPopulateUi() {
        final long alarmId = getAlarmId();
        new RetrieveAlarmTask(this, getOrCreateDbHelper(), AlarmContract.AlarmEntry.ALL_COLUMNS).execute(alarmId);
    }

    private void updateAlarmInDb(final AlarmDto dto) {
        final AlarmDbHelper alarmDbHelper = getOrCreateDbHelper();
        new UpdateAlarmInDbTask(this, alarmDbHelper).execute(dto);
    }

    private void updateAlarmInSystem(final AlarmDto dto) {
        AlarmUtils.updateAlarmInSystem(this, dto);
    }

    private void cancelAlarmInSystem(final AlarmDto dto) {
        AlarmUtils.cancelAlarmInSystem(this, dto);
    }

    @Override
    public void onInsertOperationPerformed(final long rowId) {
        // do nothing here
    }

    @Override
    public void onUpdateOperationPerformed(final boolean success) {
        final AlarmDto dto = AlarmDto.getCurrentDto();
        if (dto.isEnabled()) {
            updateAlarmInSystem(dto);
        } else {
            cancelAlarmInSystem(dto);
        }
        finish();
    }
}
