package org.ap.android.alarm;

import android.database.Cursor;
import android.os.AsyncTask;

import java.util.TimeZone;

/**
 * Created by abhi on 01.03.15.
 */
public class RetrieveAlarmTask extends AsyncTask<Long, Void, AlarmDto> {

    private final AlarmDbHelper dbHelper;
    private final String[] colsToRetrieve;
    private final AlarmRetrievedListener listener;

    private static final String TAG = RetrieveAlarmTask.class.getName();

    RetrieveAlarmTask(final AlarmRetrievedListener alarmRetrievedListener, final AlarmDbHelper dbHelper, final String[] colsToRetrieve) {
        this.listener = alarmRetrievedListener;
        this.dbHelper = dbHelper;
        this.colsToRetrieve = colsToRetrieve;
    }

    @Override
    protected AlarmDto doInBackground(final Long... alarmId) {
        final Cursor c = dbHelper.getAlarm(alarmId[0], colsToRetrieve);
        return createDto(c);
    }

    @Override
    protected void onPostExecute(final AlarmDto dto) {
        listener.handleAlarmObtainedFromDb(dto);
    }

    private AlarmDto createDto(final Cursor c) {
        if (!dbHelper.isCursorUseable(c)) {
            return null;
        }
        try {
            c.moveToFirst();
            return AlarmDto.createDto(c);
        } finally {
            c.close();
        }
    }
}
