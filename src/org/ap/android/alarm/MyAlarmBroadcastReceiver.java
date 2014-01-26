package org.ap.android.alarm;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by abhi on 14/12/13.
 */
public class MyAlarmBroadcastReceiver extends BroadcastReceiver {

	private int numOccurrences = AlarmActivity.DEFAULT_NUM_ALARM_OCCURRENCES;
	private boolean isNumOccurrencesSet = false;

	private static final String TAG = MyAlarmBroadcastReceiver.class.getName();

	@Override
	public void onReceive(Context context, Intent intent) {
		Log.i("My Receiver", "received broadcast****************");
		Log.i(TAG, "boolean flag indicating whether the number of occurrences has been set: " + isNumOccurrencesSet);

		if (!isNumOccurrencesSet && intent.hasExtra(AlarmActivity.NUM_ALARM_OCCURRENCES)) {
			numOccurrences = intent.getIntExtra(AlarmActivity.NUM_ALARM_OCCURRENCES,
					AlarmActivity.DEFAULT_NUM_ALARM_OCCURRENCES);
			isNumOccurrencesSet = true;

			Log.i(TAG, "*****number of occurrences as specified in intent: " + numOccurrences);

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
		Log.i(TAG, "This is the last alarm, after this, it has been cancelled*****");
	}

	private void handleBroadcast(Context context) {
		startActivity(context);
	}

	private void startActivity(Context context) {
		Intent intent = new Intent(context, AlarmNotificationActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(intent);
	}

	private void cancelAlarm(Context context) {
		// TODO Auto-generated method stub
		Intent bcIntent = new Intent(context, MyAlarmBroadcastReceiver.class);
		bcIntent.setClass(context, MyAlarmBroadcastReceiver.class);
		PendingIntent pi = PendingIntent.getBroadcast(context, 0, bcIntent, 0);

		AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		am.cancel(pi);
	}
}
