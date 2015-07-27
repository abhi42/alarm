package org.ap.android.alarm.ui;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import org.ap.android.alarm.R;
import org.ap.android.alarm.common.AlarmType;
import org.ap.android.alarm.common.AlarmUtils;
import org.ap.android.alarm.db.AlarmContract;
import org.ap.android.alarm.db.AlarmDbHelper;
import org.ap.android.alarm.db.IAlarmOperationInDbListener;
import org.ap.android.alarm.db.UpdateAlarmInDbTask;
import org.ap.android.alarm.dto.AlarmDto;
import org.ap.android.alarm.task.RetrieveAlarmTask;

import java.io.IOException;
import java.util.Calendar;
import java.util.TimeZone;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Created by abhi on 15.02.15.
 */
public class AlarmReceiverActivity extends ActionBarActivity implements IAlarmOperationInDbListener {

    private static final String[] COLUMNS_TO_BE_RETRIEVED = {
            AlarmContract.AlarmEntry._ID,
            AlarmContract.AlarmEntry.COLUMN_NAME_ALARM_TYPE,
            AlarmContract.AlarmEntry.COLUMN_NAME_ALARM_DESC,
            AlarmContract.AlarmEntry.COLUMN_NAME_ALARM_WEEKDAYS,
            AlarmContract.AlarmEntry.COLUMN_NAME_ALARM_START_TIME,
            AlarmContract.AlarmEntry.COLUMN_NAME_ALARM_TIME_ZONE,
            AlarmContract.AlarmEntry.COLUMN_NAME_ALARM_NUM_OCCURRENCES,
            AlarmContract.AlarmEntry.COLUMN_NAME_ALARM_INTERVAL,
            AlarmContract.AlarmEntry.COLUMN_NAME_ALARM_IS_ENABLED
    };
    private static final String TAG = AlarmReceiverActivity.class.getName();
    private Ringtone ringtone;
    private MediaPlayer mediaPlayer;
    private boolean snooze = false;
    private CountDownLatch latch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // to show the alarmDateTimeDto even if the phone is locked
        final Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        window.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
        window.addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

        setContentView(R.layout.activity_alarm_receiver);

        final String alarmDesc = this.getIntent().getStringExtra(AlarmUtils.ALARM_DESC);
        TextView txt = (TextView) findViewById(R.id.alarmReceiverTxt);
        txt.setText(txt.getText() + ": alarm description: " + alarmDesc == null ? "" : alarmDesc);

        playRingtone();
    }

    private void playRingtone() {
        final Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(this, uri);
            AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
            if (audioManager.getStreamVolume(AudioManager.STREAM_ALARM) != 0) {
                mediaPlayer.setAudioStreamType(AudioManager.STREAM_ALARM);
                mediaPlayer.setLooping(true);
                mediaPlayer.prepare();
                mediaPlayer.start();
            }
        } catch (IOException e) {
            playSimpleRingtone();
        }

    }

    private void playSimpleRingtone() {
        ringtone = RingtoneManager.getRingtone(this, RingtoneManager.getDefaultUri
                (RingtoneManager.TYPE_ALARM));
        ringtone.play();
    }

    public void onDismissClick(final View view) {
        stopMediaPlayer();
        handleDismiss();
    }

    private void stopMediaPlayer() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
        }
        if (ringtone != null) {
            ringtone.stop();
        }
    }

    private void handleDismiss() {
        // reduce the count of the alarm in the sqlite db
        retrieveAlarmFromDb();
    }

    private void retrieveAlarmFromDb() {
        final long alarmId = getAlarmId();
        if (alarmId == -1) {
            Log.d(TAG, "alarm id not present in intent, not performing any db action");
            Toast.makeText(this, "Unable to update db with alarm", Toast.LENGTH_LONG).show();
            finish();
            return;
        }
        final AlarmDbHelper dbHelper = new AlarmDbHelper(this);
        new RetrieveAlarmTask(this, dbHelper, COLUMNS_TO_BE_RETRIEVED).execute(alarmId);
    }

    private long getAlarmId() {
        return getIntent().getLongExtra(AlarmUtils.ALARM_ID_BEING_PASSED, -1);
    }

    private AlarmDto reduceAlarmCountIndDb(final AlarmDto dto) {

        final AlarmDbHelper dbHelper = new AlarmDbHelper(this);

        final long nextAlarmTime = getNextAlarmTime(dto);
        dto.setStartTimeWithoutTz(nextAlarmTime);
        dto.setNumOccurrences(dto.getNumOccurrences() - 1);

        new UpdateAlarmInDbTask(this, dbHelper).execute(dto);

        return dto;
    }

    private long getNextAlarmTimeForSnoozing(final AlarmDto dto) {
        final long startTimeWithoutTz = dto.getStartTimeWithoutTz();
        final TimeZone tz = dto.getTz();
        return getNextAlarmTime(startTimeWithoutTz, tz, AlarmUtils.getSnoozeIntervalInMilliSeconds(this));
    }

    private long getNextAlarmTime(final AlarmDto dto) {
        final long startTimeWithoutTz = dto.getStartTimeWithoutTz();
        final TimeZone tz = dto.getTz();
        final int interval = dto.getInterval();
        Log.d(TAG, "CURRENT ALARM TIME: " + AlarmUtils.formatDateTime(startTimeWithoutTz, tz.getID()));
        Log.d(TAG, "INTERVAL: " + interval);
        final long nextAlarmTime = getNextAlarmTime(startTimeWithoutTz, tz, interval * 60 * 1000);
        Log.d(TAG, "NEXT ALARM TIME: " + AlarmUtils.formatDateTime(nextAlarmTime, tz.getID()));
        return nextAlarmTime;
    }

    private long getNextAlarmTime(final long startTimeWithoutTz, final TimeZone tz, final int intervalInMilliSecs) {
        final Calendar c = AlarmUtils.getCalendar(startTimeWithoutTz, tz);
        return c.getTime().getTime() + (intervalInMilliSecs);
    }

    public void handleAlarmObtainedFromDb(final AlarmDto dto) {
        latch = new CountDownLatch(1);
        if (snooze) {
            handleAlarmSnooze(dto);
        } else {
            handleAlarmDismiss(dto);
        }
        // need to wait for the db operation to finish, since the list of alarms in the main activity is shown by retrieving all alarms from the db
        try {
            latch.await(100, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            Log.d(TAG, "exception while awaiting db action to be completed through countdownlatch", e);
        } finally {
            finish();
        }
    }

    private void handleAlarmSnooze(final AlarmDto dto) {
        final AlarmDto dtoForSnoozing = updateSnoozedAlarmInDb(dto);
        setNewAlarmInSystem(dtoForSnoozing);
    }

    private void handleAlarmDismiss(final AlarmDto dto) {
        final AlarmType type = dto.getType();
        switch (type) {
            case DAILY:
                setNextDailyAlarmIfRequired(dto);
                break;

            case WEEKLY:
                setNextWeeklyAlarm(dto);
                break;

            case MONTHLY:
                setNextMonthlyAlarm(dto);
                break;
        }

    }

    private void setNextMonthlyAlarm(final AlarmDto dto) {
        // calendar corresponding to the dto
        final Calendar calendar = Calendar.getInstance(dto.getTz());
        calendar.setTimeInMillis(dto.getStartTimeWithoutTz());

        // increment month by 1
        final int month = calendar.get(Calendar.MONTH);
        final int nextMonth = month == 11 ? 0 : month + 1;
        calendar.set(Calendar.MONTH, nextMonth);

        // set the new time in the dto
        dto.setStartTimeWithoutTz(calendar.getTimeInMillis());

        setNextAlarmInDb(dto);
    }

    private void setNextWeeklyAlarm(final AlarmDto dto) {
        final long nextAlarmTime = AlarmUtils.getNextAlarmTimeForWeeklyAlarm(dto);
        dto.setStartTimeWithoutTz(nextAlarmTime);
        setNextAlarmInDb(dto);
    }

    private void setNextAlarmInDb(final AlarmDto dto) {
        final AlarmDbHelper dbHelper = new AlarmDbHelper(this);
        new UpdateAlarmInDbTask(this, dbHelper).execute(dto);
        setNewAlarmInSystem(dto);
    }

    private void setNextDailyAlarmIfRequired(final AlarmDto dto) {
        final int numOccurrences = dto.getNumOccurrences();
        if (numOccurrences > 1) {
            final AlarmDto dtoUsedForUpdatingDb = reduceAlarmCountIndDb(dto);
            setNewAlarmInSystem(dtoUsedForUpdatingDb);
        } else {
            // disable the alarm
            handleAlarmOccurrencesFinished(dto);
        }
    }

    private AlarmDto updateSnoozedAlarmInDb(final AlarmDto dto) {
        final AlarmDbHelper dbHelper = new AlarmDbHelper(this);

        final long nextAlarmTime = getNextAlarmTimeForSnoozing(dto);
        dto.setStartTimeWithoutTz(nextAlarmTime);

        new UpdateAlarmInDbTask(this, dbHelper).execute(dto);
        return dto;
    }

    private void setNewAlarmInSystem(final AlarmDto dto) {
        AlarmUtils.createAlarmInSystem(this, dto);
    }

    private void handleAlarmOccurrencesFinished(final AlarmDto dto) {
        updateAlarmFinishedInDb(dto);
    }

    private void updateAlarmFinishedInDb(final AlarmDto dto) {
        dto.setIsEnabled(false);
        final AlarmDbHelper dbHelper = new AlarmDbHelper(this);
        new UpdateAlarmInDbTask(this, dbHelper).execute(dto);
    }

    public void onSnoozeClick(final View v) {
        stopMediaPlayer();
        handleSnooze();
    }

    private void handleSnooze() {
        snooze = true;
        retrieveAlarmFromDb();
    }

    @Override
    public void onInsertOperationPerformed(final long rowId) {
        // do nothing
    }

    @Override
    public void onUpdateOperationPerformed(final boolean success) {
        // do nothing
        latch.countDown();
    }

    @Override
    public void onRetrieveOperationPerformed(final AlarmDto dto) {
        handleAlarmObtainedFromDb(dto);
    }
}
