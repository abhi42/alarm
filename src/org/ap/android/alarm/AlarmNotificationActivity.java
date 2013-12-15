package org.ap.android.alarm;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.View;

public class AlarmNotificationActivity extends Activity {

	private Ringtone ringtone;

	private static final int NOTIFICATION_ID = 42;
	private static final String TAG = AlarmNotificationActivity.class.getName();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.notification_alarm);

		handleEvent();
	}

	private void handleEvent() {
		playRingtone();
		displayNotification();
	}

	private void playRingtone() {
		Ringtone ringtone = getOrCreateRingtone();
		ringtone.play();
	}

	private Ringtone getOrCreateRingtone() {
		if (ringtone == null) {
			ringtone = createRingtone();
		}
		return ringtone;
	}
	
	private Ringtone createRingtone() {
		Uri defaultUri = RingtoneManager
				.getDefaultUri(RingtoneManager.TYPE_ALARM);
		return RingtoneManager.getRingtone(this, defaultUri);
	}
	
	private void displayNotification() {
		NotificationCompat.Builder builder = new NotificationCompat.Builder(
				this);
		builder.setSmallIcon(R.drawable.ic_launcher);
		builder.setContentTitle("Eye Drops");
		builder.setContentText("Take your eye drops now!");

		builder.setContentIntent(createActivityIntent());

		NotificationManager notMgr = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		notMgr.notify(NOTIFICATION_ID, builder.build());
	}

	private PendingIntent createActivityIntent() {
		// this activity will be invoked when the notification is clicked
		Intent resultIntent = new Intent(this, AlarmNotificationActivity.class);

		// build a stack builder which will return to the Home screen when one
		// navigates backwards from the activity.
		TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
		// add the back stack for the Intent (but not the Intent itself)
		stackBuilder.addParentStack(AlarmNotificationActivity.class);
		// add the intent that starts the activity to the top of the stack
		stackBuilder.addNextIntent(resultIntent);
		PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0,
				PendingIntent.FLAG_UPDATE_CURRENT);
		return resultPendingIntent;
	}

	public void handleStopAlarm(View view) {
		stopRingtone();
		closeNotification();
		finish();
	}

	private void closeNotification() {
		NotificationManager notMgr = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		notMgr.cancel(NOTIFICATION_ID);		
	}

	private void stopRingtone() {
		if (ringtone != null && ringtone.isPlaying()) {
			ringtone.stop();
			Log.i(TAG, "Ringtone stopped");
		} else {
			Log.i(TAG, "Curiously, the ringtone is not playing!!!");
		}
	}
}
