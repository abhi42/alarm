package org.ap.android.alarm;

import android.app.ActionBar;
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

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by abhi on 15.02.15.
 */
public class AlarmReceiverActivity extends ActionBarActivity implements AlarmRetrievedListener,
        AlarmUpdatedListener {

    private Ringtone ringtone;
    private MediaPlayer mediaPlayer;
    private boolean snooze = false;

    private static final String[] COLUMNS_TO_BE_RETRIEVED = {
            AlarmContract.AlarmEntry._ID,
            AlarmContract.AlarmEntry.COLUMN_NAME_ALARM_DESC,
            AlarmContract.AlarmEntry.COLUMN_NAME_ALARM_START_TIME,
            AlarmContract.AlarmEntry.COLUMN_NAME_ALARM_TIME_ZONE,
            AlarmContract.AlarmEntry.COLUMN_NAME_ALARM_NUM_OCCURRENCES,
            AlarmContract.AlarmEntry.COLUMN_NAME_ALARM_INTERVAL,
            AlarmContract.AlarmEntry.COLUMN_NAME_ALARM_IS_ENABLED
    };

    private static final String TAG = AlarmReceiverActivity.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // to show the activity even if the phone is locked
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
            return;
        }
        final AlarmDbHelper dbHelper = new AlarmDbHelper(this);
        new RetrieveAlarmTask(this, dbHelper, COLUMNS_TO_BE_RETRIEVED).execute(alarmId);
    }

    private long getAlarmId() {
        return getIntent().getLongExtra(AlarmUtils.ALARM_ID_BEING_PASSED, -1);
    }

    private void reduceAlarmCountIndDb(final AlarmDto dto) {
        final long nextAlarmTime = getNextAlarmTime(dto);

        final AlarmDbHelper dbHelper = new AlarmDbHelper(this);
        final AlarmDto dtoUsedForUpdateInDb = new AlarmDto(dto.getId(), dto.getDescription(),
                nextAlarmTime, dto.getTz(), dto.getNumOccurrences() - 1, dto.getInterval(),
                dto.isEnabled());
        new UpdateAlarmInDbTask(this, dbHelper).execute(dtoUsedForUpdateInDb);
    }

    private long getNextAlarmTimeForSnoozing(final AlarmDto dto) {
        final long startTimeWithoutTz = dto.getStartTimeWithoutTz();
        final TimeZone tz = dto.getTz();
        final long nextAlarmTime = getNextAlarmTime(startTimeWithoutTz, tz, AlarmUtils.getSnoozeIntervalInMilliSeconds(this));
        return nextAlarmTime;
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

    @Override
    public void handleAlarmObtainedFromDb(final AlarmDto dto) {
        if (snooze) {
            updateSnoozedAlarmInDb(dto);
            setNewAlarmInSystemOnSnoozing(dto);
        } else {
            final int numOccurrences = dto.getNumOccurrences();
            if (numOccurrences > 1) {
                reduceAlarmCountIndDb(dto);
                setNewAlarmInSystem(dto);
            } else {
                // disable the alarm
                handleAlarmOccurrencesFinished(dto);
            }
        }
    }

    private void updateSnoozedAlarmInDb(final AlarmDto dto) {
        final long nextAlarmTime = getNextAlarmTimeForSnoozing(dto);
        final AlarmDbHelper dbHelper = new AlarmDbHelper(this);
        final AlarmDto dtoUsedForUpdateInDb = new AlarmDto(dto.getId(), dto.getDescription(),
                nextAlarmTime, dto.getTz(), dto.getNumOccurrences(), dto.getInterval(),
                dto.isEnabled());
        new UpdateAlarmInDbTask(this, dbHelper).execute(dtoUsedForUpdateInDb);
    }

    private void setNewAlarmInSystemOnSnoozing(final AlarmDto dto) {
        final long nextAlarmTime = getNextAlarmTimeForSnoozing(dto);
        final Calendar c = AlarmUtils.getCalendar(nextAlarmTime, dto.getTz());
        AlarmUtils.createInitialAlarm(this, dto.getId(), c, dto.getInterval(),
                dto.getNumOccurrences(), dto.getDescription());
    }

    private void setNewAlarmInSystem(final AlarmDto dto) {
        final long nextAlarmTime = getNextAlarmTime(dto);
        final Calendar c = AlarmUtils.getCalendar(nextAlarmTime, dto.getTz());
        AlarmUtils.createInitialAlarm(this, dto.getId(), c, dto.getInterval(),
                dto.getNumOccurrences() - 1, dto.getDescription());
    }

    private void handleAlarmOccurrencesFinished(final AlarmDto dto) {
            updateAlarmFinishedInDb(dto);
    }

    private void updateAlarmFinishedInDb(final AlarmDto dto) {
        final AlarmDto dtoUsedForUpdateInDb = new AlarmDto(dto.getId(), dto.getDescription(),
                dto.getStartTimeWithoutTz(), dto.getTz(), 0, dto.getInterval(),
                false);
        final AlarmDbHelper dbHelper = new AlarmDbHelper(this);
        new UpdateAlarmInDbTask(this, dbHelper).execute(dtoUsedForUpdateInDb);
    }

    @Override
    public void handleAlarmUpdatedInDb(final boolean updated) {
        if (!updated) {
            Toast.makeText(this, "Could not update alarm in DB", Toast.LENGTH_LONG).show();
        }
        finish();
    }

    public void onSnoozeClick(final View v) {
        stopMediaPlayer();
        handleSnooze();
    }

    private void handleSnooze() {
        snooze = true;
        retrieveAlarmFromDb();
    }
}
