package org.ap.android.alarm.task;

import android.database.Cursor;
import android.os.AsyncTask;

import org.ap.android.alarm.db.AlarmDbHelper;
import org.ap.android.alarm.db.IAlarmOperationInDbListener;
import org.ap.android.alarm.dto.AlarmDto;

/**
 * Created by abhi on 01.03.15.
 */
public class RetrieveAlarmTask extends AsyncTask<Long, Void, AlarmDto> {

    private static final String TAG = RetrieveAlarmTask.class.getName();
    private final AlarmDbHelper dbHelper;
    private final String[] colsToRetrieve;
    private final IAlarmOperationInDbListener listener;

    public RetrieveAlarmTask(final IAlarmOperationInDbListener listener, final AlarmDbHelper dbHelper, final String[] colsToRetrieve) {
        this.listener = listener;
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
        listener.onRetrieveOperationPerformed(dto);
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
