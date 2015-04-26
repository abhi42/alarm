package org.ap.android.alarm;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by abhi on 28.02.15.
 */
public class AlarmDbHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "ApaAlarm.db";
    public static final int DATABASE_VERSION = 1;

    private static final String TEXT_TYPE = " TEXT";
    private static final String INTEGER_TYPE = " INTEGER";
    private static final String DATE_TYPE = " INTEGER";
    private static final String COMMA_SEP = ", ";

    private static final String SQL_CREATE_TABLE = "CREATE TABLE " + AlarmContract.AlarmEntry
            .TABLE_NAME
            + " (" + AlarmContract.AlarmEntry._ID + " INTEGER PRIMARY KEY" + COMMA_SEP
            + AlarmContract.AlarmEntry.COLUMN_NAME_ALARM_DESC + TEXT_TYPE + COMMA_SEP
            + AlarmContract.AlarmEntry.COLUMN_NAME_ALARM_START_TIME + DATE_TYPE + COMMA_SEP
            + AlarmContract.AlarmEntry.COLUMN_NAME_ALARM_TIME_ZONE + TEXT_TYPE + COMMA_SEP
            + AlarmContract.AlarmEntry.COLUMN_NAME_ALARM_NUM_OCCURRENCES + INTEGER_TYPE + COMMA_SEP
            + AlarmContract.AlarmEntry.COLUMN_NAME_ALARM_INTERVAL + INTEGER_TYPE + COMMA_SEP
            + AlarmContract.AlarmEntry.COLUMN_NAME_ALARM_IS_ENABLED + INTEGER_TYPE
            + ")";

    private static final String TAG = AlarmDbHelper.class.getName();

    public AlarmDbHelper(final Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(final SQLiteDatabase db) {
        Log.d(TAG, SQL_CREATE_TABLE);
        db.execSQL(SQL_CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // not yet needed
    }

    Cursor getAlarm(final long alarmId, final String[] colsToRetrieve) {
        Log.d(TAG, "About to retrieve details for alarm with id " + alarmId);
        final SQLiteDatabase db = getReadableDatabase();
        final Cursor cursor = db.query(AlarmContract.AlarmEntry.TABLE_NAME, colsToRetrieve, AlarmContract.AlarmEntry._ID + "=?",
                new String[] {String.valueOf(alarmId)}, null, null, null);
        if (cursor != null) {
            Log.d(TAG, "Number of rows retrieved: " + cursor.getCount());
        }
        return cursor;
    }

    boolean isCursorUseable(final Cursor c) {
        if (c == null) {
            Log.w(TAG, "Cursor for alarm obtained from db is null");
            return false;
        }

        if (c.getCount() != 1) {
            Log.w(TAG, "Cursor retrieved 0 rows from db for the alarm to edit");
            return false;
        }
        return true;
    }
}
