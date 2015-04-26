package org.ap.android.alarm;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import java.util.Calendar;

/**
 * Created by abhi on 28.02.15.
 */
public class RegisterAlarmInDbTask extends AsyncTask<AlarmDto, Void, Long> {

    private final AlarmDbHelper dbHelper;
    private final AddAlarmActivity activity;
    private final BaseAlarmInDbTask helper;

    private static final String TAG = RegisterAlarmInDbTask.class.getName();

    RegisterAlarmInDbTask(final AddAlarmActivity activity, final AlarmDbHelper dbHelper) {
        this.activity = activity;
        this.dbHelper = dbHelper;
        helper = new BaseAlarmInDbTask();
    }

    @Override
    protected Long doInBackground(AlarmDto... dto) {
        final SQLiteDatabase db = dbHelper.getWritableDatabase();
        final ContentValues values = helper.createContentValues(dto[0]);

        Log.d(TAG, "About to insert values: " + helper.getContentValuesAsString(values));
        try {
            // -1 because row id begins from 1, where as the default _ID begins from 0
            // this is a nasty hack
            // TODO correct this!!!!!
            return db.insert(AlarmContract.AlarmEntry.TABLE_NAME, null, values);
        } finally {
            dbHelper.close();
        }
    }

    @Override
    protected void onPostExecute(final Long rowId) {
        activity.afterOperationPerformed(rowId);
    }
}
