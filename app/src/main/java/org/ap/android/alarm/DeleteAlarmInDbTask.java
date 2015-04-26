package org.ap.android.alarm;

import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;

/**
 * Created by abhi on 14.03.15.
 */
public class DeleteAlarmInDbTask extends AsyncTask<AlarmDto, Void, Boolean> {

    private final AlarmDbHelper dbHelper;
    private final BaseAlarmActivity activity;

    private static final String selection = AlarmContract.AlarmEntry._ID + " LIKE ?";

    DeleteAlarmInDbTask(final BaseAlarmActivity activity, final AlarmDbHelper dbHelper) {
        this.activity = activity;
        this.dbHelper = dbHelper;
    }

    DeleteAlarmInDbTask(final AlarmDbHelper dbHelper) {
        this(null, dbHelper);
    }

    @Override
    protected Boolean doInBackground(final AlarmDto... dtos) {
        final SQLiteDatabase db = dbHelper.getWritableDatabase();
        final String[] args = new String[] {String.valueOf(dtos[0].getId())};
        final int numRowsDeleted = db.delete(AlarmContract.AlarmEntry.TABLE_NAME, selection, args);
        return numRowsDeleted == 1 ? true : false;
    }

    @Override
    protected void onPostExecute(final Boolean aBoolean) {
        if (activity != null) {
            activity.onAlarmDeleted(aBoolean);
        }
    }
}
