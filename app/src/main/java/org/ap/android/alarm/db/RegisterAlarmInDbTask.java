package org.ap.android.alarm.db;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;

import org.ap.android.alarm.dto.AlarmDto;
import org.ap.android.alarm.task.BaseAlarmInDbTask;

/**
 * Created by abhi on 28.02.15.
 */
public class RegisterAlarmInDbTask extends AsyncTask<AlarmDto, Void, Long> {

    private static final String TAG = RegisterAlarmInDbTask.class.getName();
    private final AlarmDbHelper dbHelper;
    private final IAlarmOperationInDbListener listener;
    private final BaseAlarmInDbTask helper;

    public RegisterAlarmInDbTask(final IAlarmOperationInDbListener listener, final AlarmDbHelper dbHelper) {
        this.listener = listener;
        this.dbHelper = dbHelper;
        helper = new BaseAlarmInDbTask();
    }

    @Override
    protected Long doInBackground(final AlarmDto... dto) {
        final SQLiteDatabase db = dbHelper.getWritableDatabase();
        final ContentValues values = helper.createContentValues(dto[0]);

        Log.d(TAG, "About to insert values: " + helper.getContentValuesAsString(values));
        try {
            return db.insert(AlarmContract.AlarmEntry.TABLE_NAME, null, values);
        } finally {
            dbHelper.close();
        }
    }

    @Override
    protected void onPostExecute(final Long rowId) {
        listener.onInsertOperationPerformed(rowId);
    }
}
