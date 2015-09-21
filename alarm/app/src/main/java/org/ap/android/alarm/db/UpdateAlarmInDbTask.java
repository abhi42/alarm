package org.ap.android.alarm.db;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;

import org.ap.android.alarm.dto.AlarmDto;
import org.ap.android.alarm.task.BaseAlarmInDbTask;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by abhi on 07.03.15.
 */
public class UpdateAlarmInDbTask extends AsyncTask<AlarmDto, Void, Boolean> {

    private static final String TAG = UpdateAlarmInDbTask.class.getName();
    private final AlarmDbHelper dbHelper;
    private final IAlarmOperationInDbListener listener;
    private final BaseAlarmInDbTask helper;

    public UpdateAlarmInDbTask(final IAlarmOperationInDbListener listener, final AlarmDbHelper dbHelper) {
        this.listener = listener;
        this.dbHelper = dbHelper;
        this.helper = new BaseAlarmInDbTask();
    }

    @Override
    protected Boolean doInBackground(final AlarmDto... dto) {
        final SQLiteDatabase db = dbHelper.getWritableDatabase();
        final ContentValues values = helper.createContentValues(dto[0]);
        final String[] ids = {helper.getAlarmIdToUpdate(values)};

        Log.d(TAG, "About to update alarm in db with following details: " + helper
                .getContentValuesAsString(values));
        logStartTime(dto[0]);
        try {
            final int numRowsUpdated = db.update(AlarmContract.AlarmEntry.TABLE_NAME, values,
                    AlarmContract.AlarmEntry._ID + "=?", ids);
            return numRowsUpdated == 1;
        } finally {
            dbHelper.close();
        }
    }

    @Override
    protected void onPostExecute(final Boolean success) {
        listener.onUpdateOperationPerformed(success);
    }

    private void logStartTime(final AlarmDto dto) {
        final Date startTime = new Date(dto.getStartTimeWithoutTz());
        final Calendar c = Calendar.getInstance();
        c.setTimeZone(dto.getTz());
        c.setTime(startTime);
        Log.d(TAG, "hr: " + c.get(Calendar.HOUR_OF_DAY));
        Log.d(TAG, "min: " + c.get(Calendar.MINUTE));
    }
}
