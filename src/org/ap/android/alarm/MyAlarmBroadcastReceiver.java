package org.ap.android.alarm;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by abhi on 14/12/13.
 */
public class MyAlarmBroadcastReceiver extends BroadcastReceiver {

	private int numOccurrences = AlarmActivity.DEFAULT_NUM_ALARM_OCCURRENCES;
	private boolean isNumOccurrencesSet = false;

	private static final int NOTIFICATION_ID = 42;

	private static final String TAG = MyAlarmBroadcastReceiver.class.getName();

	@Override
	public void onReceive(Context context, Intent intent) {
		Log.i("My Receiver", "received broadcast****************");
		Log.i(TAG,
				"boolean flag indicating whether the number of occurrences has been set: "
						+ isNumOccurrencesSet);

		if (!isNumOccurrencesSet
				&& intent.hasExtra(AlarmActivity.NUM_ALARM_OCCURRENCES)) {
			numOccurrences = intent.getIntExtra(
					AlarmActivity.NUM_ALARM_OCCURRENCES,
					AlarmActivity.DEFAULT_NUM_ALARM_OCCURRENCES);
			isNumOccurrencesSet = true;

			Log.i(TAG, "*****number of occurrences as specified in intent: "
					+ numOccurrences);

			intent.removeExtra(AlarmActivity.NUM_ALARM_OCCURRENCES);
		}

		if (numOccurrences > 0) {
			handleBroadcast(context);
		} else {
			handleAlarmCancellation(context);
		}
	}

	private void handleAlarmCancellation(Context context) {
		cancelAlarm(context);
		Log.i(TAG,
				"This is the last alarm, after this, it has been cancelled*****");
	}

	private void handleBroadcast(Context context) {
//		playRingtone(context);
//		displayNotification(context);
//		Toast.makeText(context, "Eye drops", Toast.LENGTH_LONG).show();
		
		startActivity(context);
	}

	private void startActivity(Context context) {
		Intent intent = new Intent(context, AlarmNotificationActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(intent);		
//		PendingIntent activity = PendingIntent.getActivity(context, 0, intent,
//				Intent.FLAG_ACTIVITY_NEW_TASK);
	}

	private void displayNotification(Context context) {
		NotificationCompat.Builder builder = new NotificationCompat.Builder(
				context);
		builder.setSmallIcon(R.drawable.ic_launcher);
		builder.setContentTitle("Eye Drops");
		builder.setContentText("Take your eye drops now!");

		builder.setContentIntent(createActivityIntent(context));

		NotificationManager notMgr = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		notMgr.notify(NOTIFICATION_ID, builder.build());
	}

	private PendingIntent createActivityIntent(Context context) {
		// this activity will be invoked when the notification is clicked
		Intent resultIntent = new Intent(context,
				AlarmNotificationActivity.class);

		// build a stack builder which will return to the Home screen when one
		// navigates backwards from the activity.
		TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
		// add the back stack for the Intent (but not the Intent itself)
		stackBuilder.addParentStack(AlarmNotificationActivity.class);
		// add the intent that starts the activity to the top of the stack
		stackBuilder.addNextIntent(resultIntent);
		PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0,
				PendingIntent.FLAG_UPDATE_CURRENT);
		return resultPendingIntent;
	}

	private void playRingtone(Context context) {
		Uri defaultUri = RingtoneManager
				.getDefaultUri(RingtoneManager.TYPE_ALARM);
		Ringtone ringtone = RingtoneManager.getRingtone(context, defaultUri);
		ringtone.play();
	}

	private void cancelAlarm(Context context) {
		// TODO Auto-generated method stub
		Intent bcIntent = new Intent(context, MyAlarmBroadcastReceiver.class);
		bcIntent.setClass(context, MyAlarmBroadcastReceiver.class);
		PendingIntent pi = PendingIntent.getBroadcast(context, 0, bcIntent, 0);

		AlarmManager am = (AlarmManager) context
				.getSystemService(Context.ALARM_SERVICE);
		am.cancel(pi);
	}
}
