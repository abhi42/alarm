package org.ap.android.alarm.db;

import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;

import org.ap.android.alarm.dto.AlarmDto;

/**
 * Created by abhi on 14.03.15.
 */
public class DeleteAlarmInDbTask extends AsyncTask<AlarmDto, Void, Boolean> {

    private static final String selection = AlarmContract.AlarmEntry._ID + " LIKE ?";
    //    private final BaseIAlarmActivity activity;
    private final AlarmDbHelper dbHelper;

//    public DeleteAlarmInDbTask(final BaseIAlarmActivity activity, final AlarmDbHelper dbHelper) {
//        this.activity = activity;
//        this.dbHelper = dbHelper;
//    }

    public DeleteAlarmInDbTask(final AlarmDbHelper dbHelper) {
        this.dbHelper = dbHelper;
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
//        if (activity != null) {
//            activity.onAlarmDeleted(aBoolean);
//        }
    }
}
