package org.ap.android.alarm;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

/**
 * Created by abhi on 15.02.15.
 */
public class AlarmReceiverActivity extends ActionBarActivity {

    private Ringtone ringtone;
    private MediaPlayer mediaPlayer;

    private static final String TAG = AlarmReceiverActivity.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_receiver);

        final String alarmDesc = this.getIntent().getStringExtra(AlarmUtils.ALARM_DESC);
        Log.d(TAG, "alarmDesc: " + alarmDesc);
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
        if (mediaPlayer != null) {
            mediaPlayer.stop();
        }
        if (ringtone != null) {
            ringtone.stop();
        }
    }
}
